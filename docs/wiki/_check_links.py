import os, re
WIKI = os.path.dirname(os.path.abspath(__file__))
pages = {os.path.splitext(f)[0] for f in os.listdir(WIKI) if f.endswith('.md')}
# GitHub wiki pages omit .md
skip_scan = {'_facts_commands.md', '_facts_features.md', '_facts_placeholders.md',
             '_check_links.py', '_audit.py', '_gen_config_docs.py', '_intros.yml'}
missing = []
for fn in os.listdir(WIKI):
    if not fn.endswith('.md') or fn in skip_scan:
        continue
    text = open(os.path.join(WIKI, fn), encoding='utf-8').read()
    for m in re.finditer(r'\[([^\]]+)\]\(([^)]+)\)', text):
        href = m.group(2).split('#')[0].strip()
        if not href or href.startswith('http') or href.startswith('mailto'):
            continue
        target = href.replace('.md', '')
        if target not in pages:
            missing.append((fn, href, m.group(1)))
print('wiki pages:', len(pages))
print('broken:', len(missing))
for row in missing:
    print('  %s -> %s (%s)' % row)
