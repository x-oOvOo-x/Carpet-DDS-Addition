"""Offline final-artifact and production-boundary regressions."""
import importlib.util
import json
from pathlib import Path
import shutil
import tempfile
import unittest
from unittest.mock import patch
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
# The Public export records its Dev source, but intentionally excludes Dev-only
# publisher tooling. Keep production gates tested in both repository layouts.
IS_PUBLIC_EXPORT = (ROOT / '.cda-publish-source.json').is_file()


class ReleaseWiringTest(unittest.TestCase):
    def test_release_candidate_is_versioned_and_documented_as_1_8_0(self):
        properties = (ROOT / 'gradle.properties').read_text()
        self.assertIn('mod_version=1.8.0', properties)
        notes = ROOT / 'docs/release-notes/v1.8.0.md'
        self.assertTrue(notes.is_file(), '1.8.0 release notes are required')
        self.assertIn('1.8.0', notes.read_text())

    def test_both_build_modes_validate_final_jars_without_phase_tests(self):
        workflow = (ROOT / '.github/workflows/build.yml').read_text()
        self.assertEqual(workflow.count('scripts/audit_release_artifacts.py --directory build/releases'), 2)
        self.assertEqual(workflow.count('-m unittest discover -s scripts -p "test*.py"'), 2)
        for retired in ('test-verify-p5b-artifacts.py', 'test-verify-p6-runtime-artifacts.py',
                        'test_check_p8_candidate_pair_jars.py', 'check_p7_release_readiness.py'):
            self.assertNotIn(retired, workflow)

    def test_release_gate_precedes_github_write_in_both_modes(self):
        workflow = (ROOT / '.github/workflows/release.yml').read_text()
        jobs = workflow.split('      - name: Check out tagged source')[1:]
        self.assertEqual(len(jobs), 2)
        for job in jobs:
            self.assertLess(job.index('scripts/audit_release_artifacts.py --directory build/releases --production'),
                            job.index('- name: Create or update GitHub Release'))
            self.assertIn('id: release_preparation', job)
            self.assertNotIn('always()', job)
            self.assertIn("steps.release_preparation.outcome == 'success'", job)

    def test_export_artifact_audit_precedes_archive_even_for_dry_run(self):
        publisher = ROOT / 'tools/publish-public.ps1'
        if IS_PUBLIC_EXPORT:
            self.assertFalse(publisher.exists(), 'Dev publisher must be absent in Public')
            return
        script = publisher.read_text()
        self.assertNotIn('check_p7_release_readiness', script)
        self.assertLess(script.index('"scripts/audit_release_artifacts.py", "--directory", "build/releases", "--production"'),
                        script.index('@("archive"'))

    def test_executable_release_paths_no_longer_read_p7_receipts(self):
        paths = ('scripts/audit_release_artifacts.py',
                 '.github/workflows/build.yml', '.github/workflows/release.yml',
                 '.github/workflows/republish-platforms.yml')
        if not IS_PUBLIC_EXPORT:
            paths += ('tools/publish-public.ps1',)
        for path in paths:
            text = (ROOT / path).read_text()
            self.assertNotIn('check_p7_release_readiness', text, path)
            self.assertNotIn('p7-production-candidate-evidence.json', text, path)

    def test_pr_jars_use_replaceable_dev_prerelease_not_artifacts(self):
        workflow = (ROOT / '.github/workflows/build.yml').read_text()
        self.assertIn('scripts/publish-ci-pr-release.ps1', workflow)
        self.assertIn("github.event_name == 'pull_request'", workflow)
        self.assertIn("CI_TAG: ci-pr-${{ github.event.pull_request.number }}", workflow)
        self.assertNotIn('actions/upload-artifact', workflow)
        self.assertNotIn("|| 'ci-main'", workflow)
        upload_step = workflow.split('- name: Publish temporary CI PR release', 1)[1].split('- name: Stop Gradle daemons', 1)[0]
        self.assertIn("github.repository == 'x-oOvOo-x/Carpet-DDS-Addition-Dev'", upload_step)
        if IS_PUBLIC_EXPORT:
            self.assertFalse((ROOT / '.gitattributes').exists(),
                             'Dev export policy must be absent in Public')
            self.assertFalse((ROOT / 'scripts/publish-ci-pr-release.ps1').exists(),
                             'Dev PR publisher must be absent in Public')
        else:
            attributes = (ROOT / '.gitattributes').read_text()
            self.assertIn('scripts/publish-ci-pr-release.ps1 export-ignore', attributes)

    def test_pr_release_script_validates_complete_set_before_api_write(self):
        path = ROOT / 'scripts/publish-ci-pr-release.ps1'
        if IS_PUBLIC_EXPORT:
            self.assertFalse(path.exists(), 'Dev PR publisher must be absent in Public')
            return
        self.assertTrue(path.is_file(), 'PR prerelease publisher is required')
        script = path.read_text()
        self.assertIn("$env:GITHUB_EVENT_NAME -ne 'pull_request'", script)
        self.assertIn('carpet-dds-addition-v$modVersion-mc$mc.jar', script)
        self.assertLess(script.index('Expected exactly'), script.index('https://api.github.com'))
        self.assertIn('draft = $true', script)
        self.assertIn('draft = $false', script)

    def test_republish_uses_tagged_source_and_fresh_downloads(self):
        workflow = (ROOT / '.github/workflows/republish-platforms.yml').read_text()
        self.assertEqual(workflow.count('ref: ${{ env.RELEASE_TAG }}'), 2)
        # Windows and cloud each retain one CurseForge republish path.
        self.assertEqual(workflow.count('--source-root build/release-source'), 2)
        self.assertIn("Remove-Item 'build/republish' -Recurse -Force", workflow)
        self.assertIn('rm -rf build/republish', workflow)
        self.assertEqual(workflow.count('- name: Publish versions to CurseForge'), 2)
        self.assertNotIn('- name: Publish versions to Modrinth', workflow)
        self.assertNotIn('scripts/publish-modrinth.py', workflow)

    def test_release_publishes_only_github_and_optional_curseforge(self):
        workflow = (ROOT / '.github/workflows/release.yml').read_text()
        self.assertEqual(workflow.count('- name: Publish versions to CurseForge'), 2)
        self.assertNotIn('- name: Publish versions to Modrinth', workflow)
        self.assertNotIn('scripts/publish-modrinth.py', workflow)
        self.assertIn('gh release upload', workflow)

    def test_curseforge_publisher_guards_before_network(self):
        script = (ROOT / 'scripts/publish-curseforge.py').read_text()
        main = script.split('def main():')[1]
        self.assertIn('audit_release(', main)
        self.assertLess(main.index('audit_release('), main.index('game_version_ids('))


class FinalJarGuardTest(unittest.TestCase):
    def setUp(self):
        import audit_release_artifacts as guard
        self.guard = guard
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.jars = self.root / 'artifacts'
        self.jars.mkdir()
        self.write('settings.json', {'activeVersions': ['1.14.4']})
        self.write('config/dds-client-ownership-contract.json', {'revision': 12, 'targets': []})
        self.write('src/main/resources/carpet-dds-addition.mixins.json', {'client': []})
        (self.root / 'gradle.properties').write_text('mod_version=1.7.1\r\n')
        profile = self.root / 'versions/1.14.4/gradle.properties'
        profile.parent.mkdir(parents=True)
        profile.write_text('minecraft_dependency=1.14.4\ncarpet_dependency=*\nloader_dependency=>=0.16.0\njava_version=8\n')
        self.jar = self.jars / 'carpet-dds-addition-v1.7.1-mc1.14.4.jar'
        self.make_jar()

    def write(self, name, value):
        p = self.root / name
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_text(json.dumps(value))

    def make_jar(self, *, client=(), version='1.7.1', preview=False, java='>=8'):
        with ZipFile(self.jar, 'w') as jar:
            jar.writestr('fabric.mod.json', json.dumps({'id': 'carpet-dds-addition', 'version': version,
                'depends': {'minecraft': '1.14.4', 'carpet': '*', 'fabricloader': '>=0.16.0', 'java': java},
                'custom': {'dds:pre_cutover_test': True} if preview else {}}))
            jar.writestr('carpet-dds-addition.mixins.json', json.dumps({'client': list(client)}))

    def audit(self, **kwargs):
        return self.guard.audit_release(self.root, self.jars, **kwargs)

    def test_validation_only_runs_candidate_and_deepslate_audits_without_enforce(self):
        with patch.object(self.guard, 'audit_client_retirement', return_value={}) as audit, patch.object(self.guard, 'audit_deepslate'):
            result = self.audit()
            self.assertIn('ownership_contract', audit.call_args.kwargs)
            self.assertFalse(result['runtime_attestation'])

    def test_production_runs_permanent_artifact_checks_without_receipts(self):
        with patch.object(self.guard, 'audit_client_retirement', return_value={}) as audit, patch.object(self.guard, 'audit_deepslate'):
            self.audit(production=True)
            self.assertIn('ownership_contract', audit.call_args.kwargs)

    def test_historic_source_uses_its_own_matrix(self):
        self.write('src/main/resources/carpet-dds-addition.mixins.json', {'client': ['network.ClientHandshakeMixin']})
        self.make_jar(client=['network.ClientHandshakeMixin'])
        with patch.object(self.guard, 'audit_client_retirement') as retirement:
            self.audit(production=True)
            retirement.assert_not_called()

    def test_historical_source_cannot_launder_cutover_or_preview_artifact(self):
        self.write('src/main/resources/carpet-dds-addition.mixins.json', {'client': ['network.ClientHandshakeMixin']})
        for client, preview in (([], False), (['network.ClientHandshakeMixin'], True)):
            self.make_jar(client=client, preview=preview)
            with self.assertRaises(ValueError):
                self.audit(production=True)

    def test_extra_or_wrong_version_or_java_jars_fail_closed(self):
        for change in ('extra', 'version', 'java'):
            with self.subTest(change=change):
                self.make_jar(version='old' if change == 'version' else '1.7.1', java='>=21' if change == 'java' else '>=8')
                extra = self.jars / 'stale.jar'
                if change == 'extra':
                    extra.write_bytes(self.jar.read_bytes())
                with self.assertRaises(ValueError):
                    self.audit()
                extra.unlink(missing_ok=True)

    def test_java_runtime_boundaries(self):
        expected = {'1.14.4': 8, '1.17.1': 16, '1.18.2': 17, '1.20.4': 17,
                    '1.20.5': 21, '1.21.11': 21, '26.1.2': 25, '26.3': 25}
        self.assertEqual({mc: self.guard.java_level(mc) for mc in expected}, expected)

    def test_publish_version_must_match_source(self):
        with self.assertRaisesRegex(ValueError, 'version'):
            self.audit(version='1.6.0', production=True)


class CompleteCandidateFixtureTest(unittest.TestCase):
    """Exercise the real client-retirement and deepslate validators together."""
    def setUp(self):
        import audit_release_artifacts as guard
        from verify_client_retirement_artifacts import RETAINED
        self.guard = guard
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        for name in ('settings.json', 'gradle.properties', 'config/dds-client-ownership-contract.json',
                     'src/main/resources/carpet-dds-addition.mixins.json', 'src/main/rules/rules.json'):
            dest = self.root / name
            dest.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(ROOT / name, dest)
        self.targets = json.loads((self.root / 'settings.json').read_text())['activeVersions']
        self.contract = json.loads((self.root / 'config/dds-client-ownership-contract.json').read_text())
        self.version = guard.properties(self.root / 'gradle.properties')['mod_version']
        self.jars = self.root / 'artifacts'
        self.jars.mkdir()
        self.payloads = {}
        for mc in self.targets:
            dest = self.root / 'versions' / mc / 'gradle.properties'
            dest.parent.mkdir(parents=True)
            shutil.copyfile(ROOT / 'versions' / mc / 'gradle.properties', dest)
            profile = guard.properties(dest)
            wire = next(t['wireMode'] for t in self.contract['targets'] if t['minecraft'] == mc)
            payload = {name: b'fixture' for name in RETAINED}
            payload['fabric.mod.json'] = json.dumps({'id': 'carpet-dds-addition', 'version': self.version,
                'depends': {'minecraft': profile['minecraft_dependency'], 'fabricloader': profile['loader_dependency'],
                            'carpet': profile['carpet_dependency'], 'java': f'>={guard.java_level(mc)}'},
                'custom': {'dds:network_ownership': {'contract': 1, 'logical_client_owner': 'tweak', 'wire_mode': wire}}})
            payload['carpet-dds-addition.mixins.json'] = json.dumps({'client': [], 'mixins': [
                'network.ServerCustomPayloadMixin', 'network.ServerboundCustomPayloadPacketMixin',
                'network.ClientboundCustomPayloadPacketMixin', 'feature.quickcontaineraccess.ServerContainerClickMixin']})
            if mc == '1.21.11':
                payload[guard._deepslate.COMPAT_CLASS] = b'fixture'
            self.payloads[mc] = payload
            self.write_jar(mc)

    def write_jar(self, mc):
        with ZipFile(self.jars / f'carpet-dds-addition-v{self.version}-mc{mc}.jar', 'w') as jar:
            for name, value in self.payloads[mc].items():
                jar.writestr(name, value)

    def test_real_candidate_audit_accepts_all_21_without_receipt_claim(self):
        report = self.guard.audit_release(self.root, self.jars)
        self.assertEqual(report['jar_audited'], 21)
        self.assertFalse(report['runtime_attestation'])
        self.assertFalse(report['release_authorized'])

    def test_real_production_audit_accepts_all_21_without_receipt_ledger(self):
        report = self.guard.audit_release(self.root, self.jars, production=True)
        self.assertEqual(report['jar_audited'], 21)
        self.assertFalse(report['release_authorized'])

    def test_curseforge_finishes_artifact_audit_before_token_or_network(self):
        filename = 'publish-curseforge.py'
        spec = importlib.util.spec_from_file_location('publisher', ROOT / 'scripts' / filename)
        publisher = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(publisher)
        argv = [filename, '--project', '123', '--version', self.version,
                '--source-root', str(self.root), '--release-dir', str(self.jars),
                '--config', str(ROOT / 'scripts/modrinth-versions.json')]
        with patch('sys.argv', argv), patch.dict('os.environ', {'CURSEFORGE_TOKEN': ''}, clear=False), patch.object(publisher, 'request_json') as request:
            with self.assertRaisesRegex(RuntimeError, 'TOKEN is not set'):
                publisher.main()
            request.assert_not_called()

    def test_real_candidate_rejects_retired_classes_and_markers_and_deepslate(self):
        from verify_client_retirement_artifacts import REMOVED
        bad_marker = json.loads(self.payloads['26.3']['fabric.mod.json'])
        bad_marker['custom']['dds:network_ownership']['wire_mode'] = 'WRONG'
        cases = ((REMOVED[0], b'obsolete'),
                 ('fabric.mod.json', json.dumps(bad_marker)),
                 (self.guard._deepslate.COMPAT_CLASS, b'obsolete'))
        for name, value in cases:
            with self.subTest(name=name):
                payload = self.payloads['26.3']
                previous = payload.get(name)
                payload[name] = value
                self.write_jar('26.3')
                with self.assertRaises(ValueError):
                    self.guard.audit_release(self.root, self.jars)
                if previous is None:
                    del payload[name]
                else:
                    payload[name] = previous
                self.write_jar('26.3')


if __name__ == '__main__':
    unittest.main()
