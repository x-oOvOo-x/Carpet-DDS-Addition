#!/usr/bin/env python3
from pathlib import Path
import subprocess,sys

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/'src/main/java/carpetddsaddition'
for rel in ('scripts/verify-framework-core.py','scripts/verify-active-versions.py','scripts/verify-rule-docs.py'):
    code=subprocess.run([sys.executable,str(ROOT/rel)]).returncode
    if code: raise SystemExit(code)

bootstrap=JAVA/'core/bootstrap/DdsRuntimeBootstrap.java'; errors=[]
if not bootstrap.is_file(): errors.append(f'missing {bootstrap.relative_to(ROOT)}'); boot=''
else: boot=bootstrap.read_text(encoding='utf-8')
for path in sorted((JAVA/'feature').glob('*/*Lifecycle.java')):
    text=path.read_text(encoding='utf-8'); rel=path.relative_to(ROOT)
    if 'implements DdsLifecycleComponent' not in text: errors.append(f'{rel}: missing DdsLifecycleComponent')
    if path.stem+'.INSTANCE' not in boot: errors.append(f'{rel}: not registered in DdsRuntimeBootstrap')
if errors:
    print('LIFECYCLE VERIFY FAILED:',file=sys.stderr)
    for error in errors: print(f'  - {error}',file=sys.stderr)
    raise SystemExit(1)
print('DDS framework verification OK.')
