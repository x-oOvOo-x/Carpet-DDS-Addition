#!/usr/bin/env python3
from pathlib import Path
import json,sys

ROOT=Path(__file__).resolve().parents[1]
settings=json.loads((ROOT/'settings.json').read_text(encoding='utf-8'))
versions=settings.get('versions',[]); active=settings.get('activeVersions',versions); errors=[]
if not isinstance(active,list) or not active: errors.append('settings.json activeVersions must be a non-empty list')
elif len(active)!=len(set(active)): errors.append('settings.json activeVersions contains duplicates')
elif any(v not in versions for v in active): errors.append('settings.json activeVersions must be a subset of versions')
elif active!=[v for v in versions if v in active]: errors.append('settings.json activeVersions must preserve versions newest-to-oldest order')
if settings.get('coreVersion') not in active: errors.append('coreVersion must remain an active build target')
if settings.get('preprocessMainProject') not in active: errors.append('preprocessMainProject must remain an active build target')
if errors:
    print('\n'.join('ERROR: '+e for e in errors),file=sys.stderr); raise SystemExit(1)
print(f'Active Minecraft targets: {len(active)} / preprocess projects: {len(versions)}')
