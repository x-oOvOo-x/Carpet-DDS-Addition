#!/usr/bin/env python3
"""Audit final runtime JARs offline before CI, export or platform publishing.

No mode asserts runtime success or authorizes a release. Historical republishing
uses the requested tag's source root, never today's Minecraft target matrix.
"""
import argparse
import importlib.util
import json
from pathlib import Path
from zipfile import BadZipFile, ZipFile

from verify_client_retirement_artifacts import audit as audit_client_retirement

ROOT = Path(__file__).resolve().parents[1]
_spec = importlib.util.spec_from_file_location('deepslate_artifacts', ROOT / 'scripts/verify-deepslate-retirement-artifacts.py')
_deepslate = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(_deepslate)
audit_deepslate = _deepslate.audit


def properties(path):
    return dict(line.strip().split('=', 1) for line in path.read_text(encoding='utf-8').splitlines()
                if '=' in line and not line.lstrip().startswith(('#', '!')))


def java_level(mc):
    version = tuple(int(part) for part in mc.split('.'))
    for boundary, level in (((26,), 25), ((1, 20, 5), 21), ((1, 18), 17), ((1, 17), 16)):
        if version >= boundary:
            return level
    return 8


def audit_release(root, directory, *, production=False, version=None):
    root, directory = Path(root), Path(directory)
    load = lambda name: json.loads((root / name).read_text(encoding='utf-8'))
    settings = load('settings.json')
    targets = settings.get('activeVersions', settings.get('versions'))
    if not targets or len(targets) != len(set(targets)):
        raise ValueError('invalid active Minecraft targets')
    source_version = properties(root / 'gradle.properties')['mod_version'].strip()
    if version is not None and version != source_version:
        raise ValueError('publish version does not match tagged source version')
    expected = {f'carpet-dds-addition-v{source_version}-mc{mc}.jar' for mc in targets}
    found = {p.name for p in directory.glob('*.jar')}
    if found != expected:
        raise ValueError(f'final runtime JAR set differs: missing={sorted(expected-found)}, extra={sorted(found-expected)}')
    source_mixins = load('src/main/resources/carpet-dds-addition.mixins.json')
    # Historical source that still registers its legacy client hooks is checked
    # against its own tag. Post-cutover source is checked against the permanent
    # ownership-marker and logical-client-retirement invariants.
    legacy_clients = source_mixins.get('client', [])
    for mc in targets:
        path = directory / f'carpet-dds-addition-v{source_version}-mc{mc}.jar'
        with ZipFile(path) as jar:
            if jar.testzip() is not None:
                raise ValueError(f'{mc}: corrupt final JAR entry')
            metadata = json.loads(jar.read('fabric.mod.json'))
            mixins = json.loads(jar.read('carpet-dds-addition.mixins.json'))
        if metadata.get('id') != 'carpet-dds-addition' or metadata.get('version') != source_version:
            raise ValueError(f'{mc}: final JAR id/version differs from source')
        profile = properties(root / 'versions' / mc / 'gradle.properties')
        for name, value in (('minecraft', profile['minecraft_dependency']),
                            ('carpet', profile['carpet_dependency']),
                            ('fabricloader', profile['loader_dependency']),
                            ('java', f'>={java_level(mc)}')):
            if metadata.get('depends', {}).get(name) != value:
                raise ValueError(f'{mc}: incorrect {name} runtime dependency')
        custom = metadata.get('custom', {})
        marker = custom.get('dds:network_ownership', {})
        if ('dds:pre_cutover_test' in custom or
                (isinstance(marker, dict) and marker.get('pre_cutover_test', False) is not False)):
            raise ValueError(f'{mc}: preview marker in final production/candidate JAR')
        if legacy_clients and mixins.get('client') != legacy_clients:
            raise ValueError(f'{mc}: historical JAR client registrations differ from tagged source')
    if not legacy_clients:
        contract = load('config/dds-client-ownership-contract.json')
        audit_client_retirement(directory, settings, ownership_contract=contract)
        audit_deepslate(root, directory)
    return {'jar_audited': len(targets), 'historical_client_source': bool(legacy_clients),
            'runtime_attestation': False, 'release_authorized': False}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, default=ROOT)
    parser.add_argument('--directory', type=Path, required=True)
    parser.add_argument('--production', action='store_true')
    args = parser.parse_args()
    try:
        print(json.dumps(audit_release(args.root, args.directory, production=args.production), indent=2))
    except (OSError, ValueError, KeyError, TypeError, BadZipFile) as error:
        parser.exit(1, f'Final release JAR audit FAILED: {error}\n')


if __name__ == '__main__':
    main()
