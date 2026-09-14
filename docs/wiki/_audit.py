import os, re, sys, io, json

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
RES = os.path.join(ROOT, 'src', 'main', 'resources')
WIKI = os.path.join(ROOT, 'docs', 'wiki')

import yaml

def yaml_leaves(data, base=''):
    out = set()
    if isinstance(data, dict):
        for k, v in data.items():
            p = '%s.%s' % (base, k) if base else str(k)
            if isinstance(v, dict) and v:
                out |= yaml_leaves(v, p)
            else:
                out.add(p)
    return out

def wiki_paths(path):
    if not os.path.exists(path):
        return None
    out = set()
    with io.open(path, encoding='utf-8') as f:
        for line in f:
            if not line.strip().startswith('|'):
                continue
            for m in re.finditer(r'`([^`]+)`', line):
                out.add(m.group(1).strip())
    return out

ymls = sorted(f for f in os.listdir(RES) if f.endswith('.yml') and f != 'plugin.yml')
report = []
for y in ymls:
    ypath = os.path.join(RES, y)
    wpath = os.path.join(WIKI, 'Config-%s.md' % y)
    with io.open(ypath, encoding='utf-8') as f:
        data = yaml.safe_load(f) or {}
    leaves = yaml_leaves(data)
    allset = set(leaves)
    wp = wiki_paths(wpath)
    if wp is None:
        report.append((y, 'MISSING PAGE', len(leaves), 0, [], []))
        continue

    def covered(p):
        if p in wp:
            return True
        seg = p.split('.')
        if seg[-1] in wp:
            return True
        return len(seg) >= 2 and seg[-1] in wp and seg[-2] in wp

    def is_stale_key(p):
        if not re.match(r'^[A-Za-z_-][A-Za-z0-9_-]*(\.[A-Za-z0-9_-]+)+$', p):
            return False
        if p.endswith('.yml') or p.startswith('CONFIG.') or p.lower().startswith('ultimatedonutsmp2.'):
            return False
        return p not in allset

    missing = [p for p in dict.fromkeys(leaves) if not covered(p)]
    extra = [p for p in sorted(wp) if is_stale_key(p)]
    report.append((y, 'ok', len(set(leaves)), len(wp), missing, extra))

print('%-26s %7s %7s %9s %7s' % ('FILE', 'LEAVES', 'DOCD', 'UNDOCD', 'STALE'))
print('-' * 64)
for y, st, nl, nw, miss, extra in report:
    print('%-26s %7d %7d %9d %7d %s' % (y, nl, nw, len(miss), len(extra), '' if st == 'ok' else st))

print('\n\n===== DETAIL =====')
for y, st, nl, nw, miss, extra in report:
    if not miss and not extra:
        continue
    print('\n### %s  (%s)' % (y, st))
    if miss:
        print('  UNDOCUMENTED (%d):' % len(miss))
        for p in miss[:400]:
            print('    - ' + p)
        if len(miss) > 400:
            print('    ... +%d more' % (len(miss) - 400))
    if extra:
        print('  STALE / NOT IN YAML (%d):' % len(extra))
        for p in extra[:400]:
            print('    - ' + p)
        if len(extra) > 400:
            print('    ... +%d more' % (len(extra) - 400))

# orphan wiki pages
print('\n\n===== ORPHAN WIKI CONFIG PAGES =====')
for f in sorted(os.listdir(WIKI)):
    m = re.match(r'^Config-(.+\.yml)\.md$', f)
    if m and not os.path.exists(os.path.join(RES, m.group(1))):
        print('  ' + f + '  -> no such resource file')
