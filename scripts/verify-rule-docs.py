#!/usr/bin/env python3
from pathlib import Path
import json,re,sys

ROOT=Path(__file__).resolve().parents[1]
RULES=ROOT/'src/main/rules/rules.json'
DOCS=((ROOT/'docs/rules.md','en'),(ROOT/'docs/rules-zh_cn.md','zh'))
HEAD=re.compile(r'(?m)^##\s+.+?\s+\(([A-Za-z][A-Za-z0-9]*)\)\s*$')
META={
'en':{'type':re.compile(r'(?m)^- Type: `([^`]+)`\s*$'),'default':re.compile(r'(?m)^- Default: `([^`]+)`\s*$'),'version':re.compile(r'(?m)^- Supported versions: `([^`]+)`\s*$'),'categories':re.compile(r'(?m)^- Categories: (.+)$')},
'zh':{'type':re.compile(r'(?m)^- 类型：`([^`]+)`\s*$'),'default':re.compile(r'(?m)^- 默认值：`([^`]+)`\s*$'),'version':re.compile(r'(?m)^- 支持版本：`([^`]+)`\s*$'),'categories':re.compile(r'(?m)^- 分类：(.+)$')},
}
errors=[]

def fail(msg): errors.append(msg)
def value(v):
    if isinstance(v,bool): return str(v).lower()
    if v is None: return 'null'
    return str(v)
def mc(v):
    if not isinstance(v,int) or v<0: raise ValueError(f'invalid Minecraft version code: {v!r}')
    major,rest=divmod(v,10000); minor,patch=divmod(rest,100)
    return f'{major}.{minor}'+(f'.{patch}' if patch else '')
def supported(rule):
    lo,hi=rule.get('minMc'),rule.get('maxMc')
    if lo is None and hi is None: return None
    if lo is not None and hi is not None: return f'{mc(lo)}–{mc(hi)}'
    return f'{mc(lo)}+' if lo is not None else f'≤{mc(hi)}'
def sections(text):
    ms=list(HEAD.finditer(text)); out={}
    for i,m in enumerate(ms):
        name=m.group(1)
        if name in out: fail(f'duplicate rule section: {name}')
        out[name]=text[m.end():(ms[i+1].start() if i+1<len(ms) else len(text))]
    return out

def field(section,rx,label,path,name):
    m=rx.search(section)
    if not m: fail(f'{path}: {name} missing {label}'); return None
    return m.group(1).strip()

try: data=json.loads(RULES.read_text(encoding='utf-8'))
except Exception as e:
    print(f'RULE DOC VERIFY FAILED: cannot read {RULES}: {e}',file=sys.stderr); raise SystemExit(1)
rules=data.get('rules')
if not isinstance(rules,list):
    print('RULE DOC VERIFY FAILED: rules.json must contain a rules array',file=sys.stderr); raise SystemExit(1)
by_name={}
for rule in rules:
    name=rule.get('name') if isinstance(rule,dict) else None
    if not isinstance(name,str) or not name: fail('rules.json contains a rule without a valid name'); continue
    if name in by_name: fail(f'rules.json duplicate rule: {name}')
    by_name[name]=rule
expected=set(by_name)

for path,lang in DOCS:
    try: text=path.read_text(encoding='utf-8')
    except Exception as e: fail(f'cannot read {path}: {e}'); continue
    sec=sections(text); actual=set(sec)
    for name in sorted(expected-actual): fail(f'{path}: missing rule section {name}')
    for name in sorted(actual-expected): fail(f'{path}: stale/unknown rule section {name}')
    for name in sorted(expected&actual):
        rule=by_name[name]; body=sec[name]; p=META[lang]
        got_type=field(body,p['type'],'type',path,name)
        got_default=field(body,p['default'],'default',path,name)
        got_categories=field(body,p['categories'],'categories',path,name)
        if got_type is not None and got_type!=value(rule.get('type')): fail(f'{path}: {name} type {got_type!r} != rules.json {value(rule.get("type"))!r}')
        if got_default is not None and got_default!=value(rule.get('default')): fail(f'{path}: {name} default {got_default!r} != rules.json {value(rule.get("default"))!r}')
        if got_categories is not None:
            got=re.findall(r'`([^`]+)`',got_categories); want=[str(x) for x in rule.get('categories',[])]
            if got!=want: fail(f'{path}: {name} categories {got!r} != rules.json {want!r}')
        try: want_version=supported(rule)
        except ValueError as e: fail(f'rules.json: {name} {e}'); continue
        version_match=p['version'].search(body)
        if want_version is None:
            if version_match: fail(f'{path}: {name} has unsupported version metadata {version_match.group(1)!r} but rules.json has no minMc/maxMc')
        elif not version_match: fail(f'{path}: {name} missing supported versions {want_version!r}')
        elif version_match.group(1).strip()!=want_version: fail(f'{path}: {name} supported versions {version_match.group(1).strip()!r} != rules.json {want_version!r}')

if errors:
    print('RULE DOC VERIFY FAILED:',file=sys.stderr)
    for e in errors: print(f'  - {e}',file=sys.stderr)
    raise SystemExit(1)
print(f'Rule docs OK: {len(expected)} rules synchronized across rules.md and rules-zh_cn.md')
