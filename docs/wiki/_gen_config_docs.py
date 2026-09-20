# -*- coding: utf-8 -*-
"""Generate exhaustive Config-<file>.yml.md wiki pages straight from the shipped YAML.

Everything in the generated pages (key paths, data types, defaults, allowed values,
inline comments, section examples) is read from src/main/resources/*.yml so the wiki
cannot drift from the plugin. plugin.yml is intentionally excluded.
"""

import io
import os
import re
import yaml

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
RES = os.path.join(ROOT, 'src', 'main', 'resources')
WIKI = os.path.join(ROOT, 'docs', 'wiki')

BOILERPLATE = [
    re.compile(r'^The text or value for .*\.$', re.I),
    re.compile(r'^The numerical value for .*\.$', re.I),
    re.compile(r'^The decimal value for .*\.$', re.I),
    re.compile(r'^Determines whether .* is enabled or disabled\.$', re.I),
    re.compile(r'^Configuration section for .*\.$', re.I),
    re.compile(r'^The list of .*\.$', re.I),
    re.compile(r'^A list of .*\.$', re.I),
]


def is_boilerplate(text):
    t = text.strip()
    if not t:
        return True
    return any(p.match(t) for p in BOILERPLATE)


# --------------------------------------------------------------------------
# line scanner: full key path -> (comment block, source line index, indent)
# --------------------------------------------------------------------------

def scan(path):
    with io.open(path, encoding='utf-8') as f:
        lines = f.read().split('\n')
    meta = {}
    stack = []
    pending = []
    header = []
    seen_key = False
    for i, raw in enumerate(lines):
        stripped = raw.strip()
        if not stripped:
            if not seen_key:
                header.extend(pending)
            pending = []
            continue
        if stripped.startswith('#'):
            # keep interior indentation so YAML examples inside comments stay readable
            c = re.sub(r'^(\s*)#[ ]?', r'\1', raw).rstrip()
            pending.append(c)
            continue
        if stripped.startswith('- '):
            pending = []
            continue
        m = re.match(r'^(\s*)([^\s#:][^:]*?):(\s.*)?$', raw)
        if not m:
            pending = []
            continue
        indent = len(m.group(1))
        key = m.group(2).strip().strip('"\'')
        while stack and stack[-1][0] >= indent:
            stack.pop()
        stack.append((indent, key))
        full = '.'.join(k for _, k in stack)
        if full not in meta:
            meta[full] = {'comment': list(pending), 'line': i, 'indent': indent}
        if not seen_key and pending:
            # comments directly above the very first key belong to that key
            pass
        seen_key = True
        pending = []
    return lines, meta, header


DECORATION = re.compile(r'^[\s=\-*_#~]+$')


def clean_comment_block(comment_lines):
    """Drop banner/decoration lines and surrounding blanks."""
    out = [c for c in comment_lines if not DECORATION.match(c)]
    while out and not out[0].strip():
        out.pop(0)
    while out and not out[-1].strip():
        out.pop()
    return out


def split_comment(comment_lines):
    """Return (description, allowed) from a captured comment block."""
    cleaned = clean_comment_block(comment_lines)
    if any(l.startswith(' ') or ':' in l for l in cleaned) and len(cleaned) > 4:
        # looks like a structured example, not a one-line description
        return '', None
    text = ' '.join(c.strip() for c in cleaned if c.strip()).strip()
    allowed = None
    m = re.search(r'Available options:\s*(.+)$', text)
    if m:
        allowed = m.group(1).strip().rstrip('.')
        text = text[:m.start()].strip()
    if is_boilerplate(text):
        text = ''
    return text, allowed


# --------------------------------------------------------------------------
# type + default rendering
# --------------------------------------------------------------------------

def type_of(v):
    if isinstance(v, bool):
        return 'boolean'
    if isinstance(v, int):
        return 'integer'
    if isinstance(v, float):
        return 'decimal'
    if isinstance(v, str):
        return 'string'
    if isinstance(v, list):
        return 'list'
    if isinstance(v, dict):
        return 'section'
    if v is None:
        return 'empty'
    return type(v).__name__


def yaml_item(x):
    """Render one list element the way it appears in the YAML file."""
    if isinstance(x, bool):
        return 'true' if x else 'false'
    if x is None:
        return "''"
    if isinstance(x, (int, float)):
        return str(x)
    if isinstance(x, str):
        return "'%s'" % x.replace("'", "''")
    dumped = yaml.safe_dump(x, default_flow_style=True, allow_unicode=True).strip()
    return dumped[:-3].strip() if dumped.endswith('...') else dumped


def esc(s):
    return (str(s).replace('|', '\\|').replace('\n', ' ')
            .replace('\r', ' ').replace('<', '&lt;').replace('>', '&gt;'))


def render_default(v, limit=58):
    if isinstance(v, bool):
        return '`%s`' % ('true' if v else 'false')
    if v is None:
        return '_(empty)_'
    if isinstance(v, (int, float)):
        return '`%s`' % v
    if isinstance(v, str):
        if v == '':
            return "`''`"
        s = v if len(v) <= limit else v[:limit - 1] + '\u2026'
        return '`%s`' % esc(s)
    if isinstance(v, list):
        if not v:
            return '_(empty list)_'
        return '_list of %d %s_' % (len(v), 'item' if len(v) == 1 else 'items')
    if isinstance(v, dict):
        return '%d keys' % len(v)
    return '`%s`' % esc(v)


GENERIC_ALLOWED = {
    'any valid string text', 'any valid integer', 'any valid number',
    'any valid decimal number', 'any valid decimal', 'true, false',
    'any valid list', 'any valid value',
}


def allowed_for(v, declared):
    if declared and declared.strip().lower() not in GENERIC_ALLOWED:
        return esc(declared.replace('true, false', '`true`, `false`'))
    if isinstance(v, bool):
        return '`true`, `false`'
    if isinstance(v, int):
        return 'Any integer'
    if isinstance(v, float):
        return 'Any decimal number'
    if isinstance(v, str):
        return 'Any text'
    if isinstance(v, list):
        return 'A list of values'
    return '\u2014'


def humanise(key):
    k = str(key).replace('-', ' ').replace('_', ' ').strip()
    k = re.sub(r'\s+', ' ', k)
    return k.lower()


KEY_HINTS = {
    'HOST': 'Hostname or IP of the remote server.',
    'PORT': 'TCP port.',
    'PASSWORD': 'Password. Leave empty if none is set.',
    'USERNAME': 'Login username.',
    'USER': 'Login username.',
    'URI': 'Connection URI.',
    'FILE': 'Path relative to the plugin folder.',
    'CACHE-FILE': 'Local cache file, relative to the plugin folder.',
    'DATABASE': 'Database name.',
    'TYPE': 'Which option this section uses.',
    'MATERIAL': 'Bukkit `Material` name for the icon.',
    'ICON': 'Bukkit `Material` name for the icon.',
    'TITLE': 'Title text. Supports `&` colours and `&#RRGGBB` hex.',
    'MENU-TITLE': 'Menu title. Supports `&` colours and `&#RRGGBB` hex.',
    'DISPLAY-NAME': 'Item name. Supports `&` colours and `&#RRGGBB` hex.',
    'NAME': 'Display name shown to players.',
    'LORE': 'Tooltip lines under the item name.',
    'SLOT': 'Inventory slot, `0` is the top-left cell.',
    'SIZE': 'Chest size. Must be a multiple of 9 between 9 and 54.',
    'ROWS': 'Menu rows. Each row is 9 slots.',
    'MENU-SIZE': 'Chest size. Must be a multiple of 9 between 9 and 54.',
    'SOUND': 'Bukkit `Sound` name, or empty to play nothing.',
    'PERMISSION': 'Permission node. Leave empty to allow everyone.',
    'COMMAND': 'Console command, without a leading slash. Empty means none.',
    'PRICE': 'Price in the section\'s currency.',
    'PRICE-PER-UNIT': 'Price for one item.',
    'AMOUNT': 'How many to give or take.',
    'CHANCE': 'Relative weight in the pool, not a percentage.',
    'WEIGHT': 'Relative weight in the pool, not a percentage.',
    'CURRENCY': '`MONEY` or `SHARD`.',
    'DURATION': 'Lifetime in seconds.',
    'COOLDOWN': 'Wait time before the action can run again.',
    'WORLD': 'World name, as shown in `/minecraft:worlds` / Multiverse.',
    'PREFIX': 'Text prepended to every message from this feature.',
    'FORMAT': 'How the value is printed. Placeholders are listed on the feature page.',
    'ENABLED': None,  # handled below
    'FILLER': 'Material used to fill empty menu slots.',
}


def derive_desc(path, key, v, filename):
    """Fallback description when the YAML only carries boilerplate comments."""
    key = str(key)
    ku = key.upper()
    name = humanise(key)

    if ku in KEY_HINTS and KEY_HINTS[ku]:
        return KEY_HINTS[ku]
    if ku.endswith('-SOUND'):
        return KEY_HINTS['SOUND']
    if ku.endswith('-PERMISSION'):
        return KEY_HINTS['PERMISSION']
    if ku.endswith('-COMMAND'):
        return KEY_HINTS['COMMAND']
    if ku.endswith('-SLOT'):
        return KEY_HINTS['SLOT']

    if isinstance(v, bool):
        if ku in ('ENABLED', 'ENABLE'):
            parent = path.split('.')[-2] if '.' in path else filename
            return 'Turns the `%s` section on or off.' % parent
        return 'On/off for %s.' % name

    if isinstance(v, int) and not isinstance(v, bool):
        extra = ''
        if ku.endswith('-MS') or 'MILLIS' in ku:
            extra = ' Milliseconds.'
        elif 'TICK' in ku:
            extra = ' Ticks (20 = 1 second).'
        elif 'SECONDS' in ku or ku.endswith('-SECONDS') or ku.endswith('-SEC'):
            extra = ' Seconds.'
        elif 'MINUTES' in ku:
            extra = ' Minutes.'
        elif ku.endswith('-RADIUS') or ku == 'RADIUS':
            extra = ' Blocks.'
        return '%s.%s' % (name[0].upper() + name[1:], extra)

    if isinstance(v, list):
        return 'The %s list.' % name
    if isinstance(v, dict):
        return 'Options for %s, listed below.' % name
    return name[0].upper() + name[1:] + '.'


# --------------------------------------------------------------------------
# collection detection
# --------------------------------------------------------------------------

ID_PREFERENCE = ['name', 'display-name', 'displayname', 'title', 'type', 'material',
                 'enchantment', 'slot', 'page', 'price', 'price-per-unit', 'amount',
                 'chance', 'weight', 'durations', 'permission', 'currency', 'level']


def detect_collection(node, min_entries=5):
    """Detect a map of sibling entries that genuinely share one schema.

    Siblings that do not fit the shared schema are returned as `outliers` so the
    caller can document them as ordinary subsections instead of pretending they
    are entries in a list.
    """
    from collections import Counter
    dict_children = {k: v for k, v in node.items() if isinstance(v, dict) and v}
    if len(dict_children) < min_entries:
        return None

    def count(children):
        c = Counter()
        for v in children.values():
            for k in v.keys():
                c[k] += 1
        return c

    # first pass: rough signature over every sibling
    rough = count(dict_children)
    n = len(dict_children)
    core0 = {k for k, c in rough.items() if c >= n * 0.5}
    if not core0:
        return None
    seed = {k: v for k, v in dict_children.items()
            if len(core0 & set(v.keys())) >= max(1, len(core0) * 0.6)}
    if len(seed) < min_entries:
        return None

    # second pass: tighten the schema using only the sibling group that agreed
    tight = count(seed)
    core = {k for k, c in tight.items() if c >= len(seed) * 0.8}
    if len(core) < 2:
        return None
    entries = {k: v for k, v in dict_children.items()
               if len(core & set(v.keys())) >= max(1, len(core) * 0.6)}
    if len(entries) < min_entries:
        return None
    outliers = {k: v for k, v in node.items() if k not in entries and isinstance(v, dict)}
    return {'entries': entries, 'core': sorted(core, key=str),
            'counts': count(entries), 'n': len(entries), 'outliers': outliers}


def collection_index(coll):
    """Compact index table of every entry key with its most identifying fields."""
    counter = coll['counts']
    n = coll['n']
    cols = []
    lower = {str(k).lower(): k for k in counter}
    for want in ID_PREFERENCE:
        if want in lower and counter[lower[want]] >= n * 0.5:
            cols.append(lower[want])
        if len(cols) >= 3:
            break
    header = '| Entry key | ' + ' | '.join('`%s`' % esc(c) for c in cols) + ' |'
    sep = '| :--- | ' + ' | '.join(':---' for _ in cols) + ' |'
    out = [header, sep]
    for ek, ev in coll['entries'].items():
        cells = []
        for c in cols:
            v = ev.get(c)
            if isinstance(v, list):
                v = ', '.join(str(x) for x in v)
            if v is None:
                cells.append('\u2014')
            else:
                s = str(v)
                cells.append('`%s`' % esc(s if len(s) <= 46 else s[:45] + '\u2026'))
        out.append('| `%s` | %s |' % (esc(ek), ' | '.join(cells)))
    return out


# --------------------------------------------------------------------------
# page emitter
# --------------------------------------------------------------------------

OPTION_HEADER = ['| Option path | Type | Accepted values | Default | What it does |',
                 '| :--- | :--- | :--- | :--- | :--- |']
SCHEMA_HEADER = ['| Key | Type | Accepted values | Required? | What it does |',
                 '| :--- | :--- | :--- | :--- | :--- |']


class Page(object):
    def __init__(self, filename, overrides=None):
        self.filename = filename
        self.overrides = overrides or {}
        self.path = os.path.join(RES, filename)
        self.lines, self.meta, self.header = scan(self.path)
        with io.open(self.path, encoding='utf-8') as f:
            self.data = yaml.safe_load(f) or {}
        self.out = []
        self.option_count = 0
        self.entry_count = 0
        self.emitted = set()

    def mark(self, path, value):
        """Record that `path` is described somewhere on the page."""
        if isinstance(value, dict):
            for k, v in value.items():
                self.mark('%s.%s' % (path, k), v)
        else:
            self.emitted.add(path)

    def yaml_leaves(self, node=None, base=''):
        node = self.data if node is None else node
        out = set()
        if isinstance(node, dict):
            for k, v in node.items():
                p = '%s.%s' % (base, k) if base else str(k)
                if isinstance(v, dict) and v:
                    out |= self.yaml_leaves(v, p)
                else:
                    out.add(p)
        return out

    def w(self, s=''):
        self.out.append(s)

    def section_source(self, key, cap=48):
        """Verbatim YAML for one top-level section."""
        m = self.meta.get(key)
        if not m:
            return None
        start = m['line']
        # include the comment block above
        i = start - 1
        while i >= 0 and (self.lines[i].strip().startswith('#')):
            i -= 1
        start = i + 1
        end = start + 1
        for j in range(m['line'] + 1, len(self.lines)):
            line = self.lines[j]
            if line.strip() and not line.startswith(' ') and not line.startswith('\t'):
                break
            end = j + 1
        block = self.lines[start:end]
        while block and not block[-1].strip():
            block.pop()
        truncated = False
        if len(block) > cap:
            block = block[:cap]
            truncated = True
        return '\n'.join(block), truncated

    def describe(self, full, key, value):
        """Description precedence: manual override > real YAML comment > derived."""
        ov = self.overrides
        parts = full.split('.')
        wildcard = '.'.join(parts[:-2] + ['*', parts[-1]]) if len(parts) >= 3 else None
        for cand in (full, wildcard, key):
            if cand and cand in ov:
                return ov[cand], None
        desc, allowed = '', None
        cm = self.meta.get(full)
        if cm:
            desc, allowed = split_comment(cm['comment'])
        if not desc:
            desc = derive_desc(full, key, value, self.filename)
        return desc, allowed

    def compact_scalar_table(self, node, base, depth, heading):
        """Three-column grid for big flat maps (worth prices, sound names, ...)."""
        h = '#' * min(depth, 6)
        items = list(node.items())
        self.w('%s %s (%d entries)' % (h, heading, len(items)))
        self.w()
        self.w('| Key | Value | Key | Value | Key | Value |')
        self.w('| :--- | ---: | :--- | ---: | :--- | ---: |')
        for i in range(0, len(items), 3):
            chunk = items[i:i + 3]
            cells = []
            for k, v in chunk:
                cells.append('`%s`' % esc(k))
                cells.append(render_default(v, 40))
                self.mark('%s.%s' % (base, k) if base else str(k), v)
            while len(cells) < 6:
                cells.extend(['', ''])
            self.w('| %s |' % ' | '.join(cells))
        self.option_count += len(items)
        self.w()

    # ---- recursive documentation of one mapping ----
    def document(self, node, base, depth):
        h = '#' * min(depth, 6)
        if not node:
            self.w('This section ships empty. The plugin fills it in as you create entries '
                   'in-game, so there is nothing to configure by hand here.')
            self.w()
            self.mark(base, None)
            return
        scalars = [(k, v) for k, v in node.items() if not isinstance(v, dict)]
        maps = {k: v for k, v in node.items() if isinstance(v, dict)}

        # a large flat map of plain values reads far better as a compact grid
        if scalars and not maps and len(scalars) >= 25 and \
                all(not isinstance(v, (list, dict)) for _, v in scalars):
            self.compact_scalar_table(node, base, depth, 'Values')
            return

        if scalars:
            rows = []
            lists = []
            for k, v in scalars:
                full = '%s.%s' % (base, k) if base else k
                desc, allowed = self.describe(full, k, v)
                rows.append('| `%s` | `%s` | %s | %s | %s |' % (
                    esc(full), type_of(v), allowed_for(v, allowed), render_default(v), esc(desc)))
                self.option_count += 1
                self.mark(full, v)
                if isinstance(v, list) and v:
                    lists.append((full, v))
            self.w('%s Options' % h)
            self.w()
            self.out.extend(OPTION_HEADER)
            self.out.extend(rows)
            self.w()
            for full, v in lists:
                self.w('<details>')
                self.w('<summary>Default contents of <code>%s</code> (%d %s)</summary>'
                       % (esc(full), len(v), 'item' if len(v) == 1 else 'items'))
                self.w()
                self.w('```yaml')
                self.w('%s:' % str(full).split('.')[-1])
                for item in v[:60]:
                    self.w('  - %s' % yaml_item(item))
                if len(v) > 60:
                    self.w('  # ... %d more' % (len(v) - 60))
                self.w('```')
                self.w()
                self.w('</details>')
                self.w()

        if not maps:
            return

        coll = detect_collection(maps)
        if coll:
            for k, v in coll['outliers'].items():
                full = '%s.%s' % (base, k) if base else str(k)
                self.w('%s `%s`' % (h, full))
                self.w()
                cm = self.meta.get(full)
                d = self.overrides.get(full) or (split_comment(cm['comment'])[0] if cm else '')
                if d:
                    self.w(d)
                    self.w()
                self.document(v, full, depth + 1)
            self.entry_count += coll['n']
            label = base.split('.')[-1] if base else self.filename
            self.w('%s Entry schema (%d entries)' % (h, coll['n']))
            self.w()
            self.w('Each entry under `%s` is keyed by a name you choose, and every entry accepts '
                   'the same options:' % (base or label))
            self.w()
            self.w('%s Shipped entries' % h)
            self.w()
            self.out.extend(collection_index(coll))
            self.w()
            samples = [('%s.%s' % (base, ek) if base else str(ek), ev)
                       for ek, ev in coll['entries'].items()]
            self.document_schema(samples, '%s.<entry>' % (base or label), depth)
            return

        for k, v in maps.items():
            full = '%s.%s' % (base, k) if base else k
            cm = self.meta.get(full)
            desc = ''
            if full in self.overrides:
                desc = self.overrides[full]
            elif cm:
                desc, _ = split_comment(cm['comment'])
            self.w('%s `%s`' % (h, full))
            self.w()
            if desc:
                self.w(desc)
                self.w()
            self.document(v, full, depth + 1)

    def document_schema(self, samples, display, depth):
        """Document a repeated block. `samples` is a list of (real path, mapping) pairs.

        Recurses into nested blocks so that every leaf under a repeated entry ends up
        described somewhere on the page.
        """
        from collections import Counter
        h = '#' * min(depth, 6)
        dicts = [(p, d) for p, d in samples if isinstance(d, dict) and d]
        if not dicts:
            return
        counter = Counter()
        for _, d in dicts:
            for k in d:
                counter[k] += 1
        n = len(dicts)
        values = [v for _, d in dicts for v in d.values()]
        nested_values = sum(1 for v in values if isinstance(v, dict) and v)

        # Many differently-named children that all hold the same kind of block:
        # document the shared block once and list the names that ship with it.
        if len(counter) > max(6, 1.5 * n) and values and nested_values >= 0.8 * len(values):
            self.w('%s `%s` \u2014 one block per key' % (h, display))
            self.w()
            self.w('Keys under `%s` are identifiers rather than fixed options; every one of '
                   'them takes the same block described below. The shipped file defines %d:'
                   % (display, len(counter)))
            self.w()
            self.w(', '.join('`%s`' % esc(k) for k in sorted(counter, key=str)))
            self.w()
            inner = []
            for p, d in dicts:
                for k, v in d.items():
                    if isinstance(v, dict) and v:
                        inner.append(('%s.%s' % (p, k), v))
                    else:
                        self.mark('%s.%s' % (p, k), v)
            self.document_schema(inner, '%s.<key>' % display, depth)
            return

        rows = []
        nested_keys = []
        for k in sorted(counter, key=lambda x: (-counter[x], str(x))):
            sample = sample_path = None
            for p, d in dicts:
                if k in d:
                    sample, sample_path = d[k], '%s.%s' % (p, k)
                    break
            if isinstance(sample, dict) and sample:
                nested_keys.append(k)
            desc, allowed = self.describe(sample_path, k, sample)
            presence = 'Required' if counter[k] == n else 'Optional (%d/%d)' % (counter[k], n)
            rows.append('| `%s` | `%s` | %s | %s | %s |' % (
                esc(k), type_of(sample), allowed_for(sample, allowed), presence, esc(desc)))
            for p, d in dicts:
                if k in d and not (isinstance(d[k], dict) and d[k]):
                    self.mark('%s.%s' % (p, k), d[k])
        self.option_count += len(rows)
        self.out.extend(SCHEMA_HEADER)
        self.out.extend(rows)
        self.w()

        for k in nested_keys:
            sub = [('%s.%s' % (p, k), d[k]) for p, d in dicts
                   if isinstance(d.get(k), dict) and d[k]]
            self.w('%s `%s.%s`' % ('#' * min(depth + 1, 6), display, k))
            self.w()
            self.document_schema(sub, '%s.%s' % (display, k), depth + 1)

    def build(self, entry=None):
        entry = entry or {}
        intro = entry.get('intro')
        self.w('# `%s`' % self.filename)
        self.w()
        if intro:
            self.w(intro.strip())
            self.w()
        useful_header = [c for c in clean_comment_block(self.header)
                         if not c.lower().startswith('ultimatedonutsmp2 setup')
                         and 'keep indentation' not in c.lower()]
        if useful_header and len(' '.join(useful_header)) > 40:
            self.w('Comment at the top of the shipped file:')
            self.w()
            self.w('```text')
            for c in useful_header:
                self.w(c)
            self.w('```')
            self.w()

        top = list(self.data.keys())
        # placeholder, filled in after the body is generated
        facts_at = len(self.out)
        self.w('')
        self.w()

        self.w('## Sections in this file')
        self.w()
        self.w('| Section | Type | Contents |')
        self.w('| :--- | :--- | :--- |')
        for k in top:
            v = self.data[k]
            if isinstance(v, dict):
                kind = 'section'
                contents = '%d keys' % len(v)
            elif isinstance(v, list):
                kind = 'list'
                contents = '%d entries' % len(v)
            else:
                kind = type_of(v)
                contents = render_default(v)
            anchor = re.sub(r'[^a-z0-9]+', '-', ('section-%s' % k).lower()).strip('-')
            self.w('| [`%s`](#%s) | %s | %s |' % (esc(k), anchor, kind, contents))
        self.w()
        self.w('---')
        self.w()

        for k in top:
            v = self.data[k]
            cm = self.meta.get(k)
            desc = ''
            if cm:
                desc, _ = split_comment(cm['comment'])
            self.w('## Section: `%s`' % k)
            self.w()
            if desc:
                self.w(desc)
                self.w()
            if isinstance(v, dict):
                self.document(v, k, 3)
            elif isinstance(v, list):
                self.w('A list of %d values:' % len(v))
                self.w()
                self.w('```yaml')
                self.w('%s:' % k)
                for item in v[:40]:
                    self.w('  - %s' % item)
                if len(v) > 40:
                    self.w('  # ... %d more' % (len(v) - 40))
                self.w('```')
                self.w()
                self.option_count += 1
                self.mark(str(k), v)
            else:
                self.out.extend(OPTION_HEADER)
                self.w('| `%s` | `%s` | %s | %s | %s |' % (
                    esc(k), type_of(v), allowed_for(v, None), render_default(v),
                    esc(desc or derive_desc(k, k, v, self.filename))))
                self.w()
                self.option_count += 1
                self.mark(str(k), v)

            src = self.section_source(k)
            if src:
                block, truncated = src
                self.w('<details>')
                self.w('<summary>Default <code>%s</code> block as shipped</summary>' % esc(k))
                self.w()
                self.w('```yaml')
                self.w(block)
                if truncated:
                    self.w('# ... section continues, see the file on disk for the full block')
                self.w('```')
                self.w()
                self.w('</details>')
                self.w()
            self.w('---')
            self.w()

        reload = (entry.get('reload') or '/ultimatedonutsmp2 reload').strip('`')
        loc = entry.get('localized')
        facts = ['| | |', '| :--- | :--- |',
                 '| **On disk** | `plugins/UltimateDonutSmp2/%s` |' % self.filename]
        if entry.get('commands'):
            facts.append('| **Commands** | %s |' % entry['commands'])
        facts.append('| **Player-facing text** | %s |' % (
            'Edit `%s` in `languages/<locale>.yml` \u2014 not this file' % loc
            if loc else 'Not translated. Edit this file directly.'))
        facts.append('| **Reload** | `%s` |' % reload)
        self.out[facts_at] = '\n'.join(facts)

        self.w('Defaults above match the file shipped in the jar.')
        self.w()
        return '\n'.join(self.out)


def load_meta():
    path = os.path.join(WIKI, '_intros.yml')
    if not os.path.exists(path):
        return {}
    with io.open(path, encoding='utf-8') as f:
        return yaml.safe_load(f) or {}


INDEX_HEADER = """# Configuration Reference

{count} YAML files live in `plugins/UltimateDonutSmp2/`. Each one has its own page with every
key, type, and shipped default. `plugin.yml` is not listed — edit commands through
[Commands & Permissions](Commands-and-Permissions), not that file.

## Rules that apply to every file

- Use spaces, never tabs.
- `/ultimatedonutsmp2 reload` (also `/uds reload`) picks up most edits. `dialog.yml` →
  `PAUSE-SCREEN` needs a full restart.
- Dialog screens need a **Java client 1.21.6 or newer**. 1.21.5 and older render a broken UI.
  See [Dialog API & older clients](Dialog-API-and-Older-Clients).
- New keys from an update are merged in. Your old file is copied to `config-backups/`.
  Crates you defined, plus `pvp.yml` `ARENA`/`KITS` and the arena blocks in `duels.yml` /
  `ffa.yml`, are never overwritten.
- If a file is marked **translated**, change wording in `languages/<locale>.yml` and leave
  this file for layout and numbers. See [Localization & Messages](Localization-and-Messages).

## I want to\u2026

| \u2026 | Open |
| :--- | :--- |
| Change language, features, chat, combat, teleports | [config.yml](Config-config.yml) |
| Switch storage, or join servers with Redis | [database.yml](Config-database.yml), [network.yml](Config-network.yml) |
| Change sell prices | [worth.yml](Config-worth.yml) |
| Change the shop / shard shop | [shop.yml](Config-shop.yml) |
| Move a menu button | [menus.yml](Config-menus.yml) |
| Set up crates or spawners | [crates.yml](Config-crates.yml), [spawners.yml](Config-spawners.yml) |
| Set up ranked PvP, duels, or FFA | [pvp.yml](Config-pvp.yml), [duels.yml](Config-duels.yml), [ffa.yml](Config-ffa.yml) |
| Change staff tools or punishments | [staff-mode.yml](Config-staff-mode.yml), [offenses.yml](Config-offenses.yml) |
| Change the sidebar | [scoreboard.yml](Config-scoreboard.yml) |
| Change a sound | [sounds.yml](Config-sounds.yml) |

## Every file

| File | What it is for | Translated? |
| :--- | :--- | :---: |
{rows}

---
"""


BLURBS = {
    'amethyst-tools.yml': 'Timed Drill, Chopper, and other premium tools',
    'anvil-moderation.yml': 'Block slurs and ads in anvil renames',
    'auction-house.yml': 'Rules for `/ah` listings, claims, and bots',
    'config.yml': 'Language, features, chat, combat, teleports, shards',
    'crates.yml': 'Crate keys, animations, and weighted rewards',
    'database.yml': 'SQLite / MySQL / MongoDB, plus optional Redis',
    'death-messages.yml': 'Wording for custom death messages',
    'dialog.yml': 'Native 1.21.6+ dialog screens (pay, homes, pause)',
    'discord.yml': 'Punishment and staff webhooks (no bot)',
    'duels.yml': '1v1 queue, arenas, rollback, cross-server',
    'enchantments.yml': 'Orders enchantment-picker layout',
    'ender-chest.yml': 'Network ender chest size and `/ecsee`',
    'ffa.yml': 'Instanced FFA rollback and player state',
    'filter.yml': 'Item categories for AH / Orders browse buttons',
    'freeze.yml': 'What a frozen player can still do',
    'hide.yml': 'Scramble names and `/disguise` skins',
    'invsee.yml': 'Staff inventory inspector layout',
    'menus.yml': 'Every chest GUI layout',
    'network.yml': 'This server\'s name, `/servers`, maintenance',
    'offenses.yml': 'Preset `/offend` punishments and wipe flag',
    'orders.yml': 'Buy-order board, matching, Bedrock forms',
    'pvp.yml': 'Ranked arena, Elo, kits, schematic reset',
    'rtp.yml': 'Random teleport worlds, cache, and queue',
    'scoreboard.yml': 'Sidebar layouts (MODERN / LEGACY)',
    'server-wipe.yml': 'Season reset worlds and confirmation token',
    'shop.yml': 'Quick Buy + shard shop',
    'sounds.yml': 'Every sound the plugin plays',
    'spawn-stash.yml': 'Staff bait chests and alerts',
    'spawners.yml': 'Stacked spawners that store drops',
    'staff-mode.yml': 'Staff hotbar, vanish, fake players',
    'worth.yml': 'Sell prices and worth tooltips',
}


def write_index(meta, stats):
    rows = []
    for fn, sections, options, localized in stats:
        entry = meta.get(fn) or {}
        purpose = entry.get('blurb') or BLURBS.get(fn) or '\u2014'
        rows.append('| [`%s`](Config-%s) | %s | %s |' % (
            fn, fn, purpose, 'Yes' if localized else 'No'))
    text = INDEX_HEADER.format(count=len(stats), rows='\n'.join(rows))
    text += ('\n_Option counts are generated from `src/main/resources`, so they track the '
             'shipped files exactly._\n')
    with io.open(os.path.join(WIKI, 'Configuration-Reference.md'), 'w',
                 encoding='utf-8', newline='\n') as f:
        f.write(text)


def main():
    meta = load_meta()
    files = sorted(f for f in os.listdir(RES) if f.endswith('.yml') and f != 'plugin.yml')
    total_leaves = total_missing = 0
    gaps = []
    stats = []
    for fn in files:
        entry = meta.get(fn) or {}
        page = Page(fn, entry.get('keys'))
        text = page.build(entry)
        out = os.path.join(WIKI, 'Config-%s.md' % fn)
        with io.open(out, 'w', encoding='utf-8', newline='\n') as f:
            f.write(text)

        stats.append((fn, len(page.data), page.option_count, bool(entry.get('localized'))))
        leaves = page.yaml_leaves()
        missing = sorted(leaves - page.emitted)
        total_leaves += len(leaves)
        total_missing += len(missing)
        if missing:
            gaps.append((fn, missing))
        print('%-26s leaves=%-5d documented=%-5d missing=%-4d entries=%-4d' % (
            fn, len(leaves), len(leaves) - len(missing), len(missing), page.entry_count))

    write_index(meta, stats)
    print('\nwrote Configuration-Reference.md (%d files indexed)' % len(stats))
    print('\nTOTAL: %d config keys, %d documented, %d missing (%.2f%% coverage)' % (
        total_leaves, total_leaves - total_missing, total_missing,
        100.0 * (total_leaves - total_missing) / max(1, total_leaves)))
    for fn, missing in gaps:
        print('\n%s missing %d:' % (fn, len(missing)))
        for p in missing[:25]:
            print('   ' + p)
        if len(missing) > 25:
            print('   ... +%d more' % (len(missing) - 25))


if __name__ == '__main__':
    main()
