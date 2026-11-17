"""
Generates docs/release_notes.html from VersionHistory.md.

Run from the repository root after updating VersionHistory.md:

    python scripts/release_notes.py

VersionHistory.md format understood by this script:

    ## Version 5.0 (Lingonberry)          <- major version and codename
    #### Version 5.0.1 [Nov 2026]          <- release and date
    - module: description #38            <- change; "module:" prefix and "#NN" are optional
"""
import html
import io
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SOURCE = os.path.join(ROOT, "VersionHistory.md")
TARGET = os.path.join(ROOT, "docs", "release_notes.html")
ISSUES = "https://github.com/midhunhk/lib-aeapps/issues/"

MAJOR = re.compile(r"^##\s+Version\s+(\S+)\s*\((.+?)\)\s*$")
RELEASE = re.compile(r"^####\s+Version\s+([^\s\[]+)\s*\[(.+?)\]\s*$")
ITEM = re.compile(r"^\s*-\s+(.*\S)\s*$")
MODULE = re.compile(r"^([a-z][a-z0-9-]*):\s+(.*)$")


def inline(text):
    """Escape a change description and turn `code` and #NN into markup."""
    parts = re.split(r"(`[^`]+`)", text)
    out = []
    for part in parts:
        if part.startswith("`") and part.endswith("`") and len(part) > 1:
            out.append("<code>%s</code>" % html.escape(part[1:-1]))
        else:
            escaped = html.escape(part)
            out.append(re.sub(r"#(\d+)\b",
                              lambda m: '<a href="%s%s">#%s</a>' % (ISSUES, m.group(1), m.group(1)),
                              escaped))
    return "".join(out)


def parse():
    majors = []
    with io.open(SOURCE, encoding="utf-8") as f:
        for line in f:
            line = line.rstrip("\n")
            m = MAJOR.match(line)
            if m:
                majors.append({"version": m.group(1), "codename": m.group(2), "releases": []})
                continue
            m = RELEASE.match(line)
            if m and majors:
                majors[-1]["releases"].append({"version": m.group(1), "date": m.group(2), "items": []})
                continue
            m = ITEM.match(line)
            if m and majors and majors[-1]["releases"]:
                majors[-1]["releases"][-1]["items"].append(m.group(1))
    return majors


def render(majors):
    out = []
    for major in majors:
        out.append('            <h2 class="major" id="v%s">Version %s <span class="codename">%s</span></h2>'
                   % (html.escape(major["version"]), html.escape(major["version"]), html.escape(major["codename"])))
        for release in major["releases"]:
            out.append('            <section class="release">')
            out.append('                <div class="release-version">%s<span class="release-date">%s</span></div>'
                       % (html.escape(release["version"]), html.escape(release["date"])))
            out.append("                <ul>")
            for item in release["items"]:
                m = MODULE.match(item)
                if m:
                    out.append('                    <li><span class="module-tag">%s</span>%s</li>'
                               % (html.escape(m.group(1)), inline(m.group(2))))
                else:
                    out.append("                    <li>%s</li>" % inline(item))
            out.append("                </ul>")
            out.append("            </section>")
    return "\n".join(out)


PAGE = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Release notes · lib-aeapps</title>
    <meta name="description" content="Release notes for every version of the lib-aeapps Android library.">
    <meta name="theme-color" content="#1e0757">
    <link rel="icon" href="assets/img/icon/favicon.ico">
    <link rel="stylesheet" href="assets/css/site.css">
</head>
<body class="release-page">
<a class="skip-link" href="#main">Skip to content</a>

<!-- Generated from VersionHistory.md by scripts/release_notes.py. Do not edit by hand. -->

<header class="site-header">
    <div class="container">
        <a class="brand" href="index.html" aria-label="lib-aeapps home">
            <img src="assets/img/icon/lib_ae_apps.svg" alt="lib-aeapps" width="169" height="32">
        </a>
        <nav class="site-nav" aria-label="Main">
            <a href="index.html#get-started">Get started</a>
            <a href="index.html#modules">Modules</a>
            <a href="api/index.html">API</a>
            <a href="release_notes.html" aria-current="page">Release notes</a>
            <a href="https://github.com/midhunhk/lib-aeapps">GitHub</a>
        </nav>
    </div>
</header>

<main id="main">
    <section class="hero">
        <div class="container">
            <h1>Release notes</h1>
            <p class="lead">Every version of lib-aeapps, newest first. Development versions are named after fruits.</p>
        </div>
    </section>

    <section class="section">
        <div class="container">
%s
        </div>
    </section>
</main>

<footer class="site-footer">
    <div class="container">
        <div class="labs">
            <img src="resources/v4/icons/app_labs_icon_white.png" alt="" width="48" height="48">
            <p>lib-aeapps by Ae App Labs.<br>Licensed under the
                <a href="https://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>.</p>
        </div>
        <div>
            <h2>Project</h2>
            <ul>
                <li><a href="https://github.com/midhunhk/lib-aeapps">Source code</a></li>
                <li><a href="https://github.com/midhunhk/lib-aeapps/issues">Issues</a></li>
                <li><a href="https://github.com/midhunhk/lib-aeapps/blob/main/VersionHistory.md">VersionHistory.md</a></li>
            </ul>
        </div>
        <div>
            <h2>Docs</h2>
            <ul>
                <li><a href="api/index.html">API reference</a></li>
                <li><a href="https://github.com/midhunhk/lib-aeapps/wiki">Wiki</a></li>
                <li><a href="https://github.com/midhunhk/lib-aeapps/wiki/Codenames">Codenames</a></li>
            </ul>
        </div>
    </div>
</footer>
</body>
</html>
"""


def main():
    majors = parse()
    with io.open(TARGET, "w", encoding="utf-8", newline="\n") as f:
        f.write(PAGE % render(majors))
    releases = sum(len(m["releases"]) for m in majors)
    print("Wrote %s: %d major versions, %d releases" % (os.path.relpath(TARGET, ROOT), len(majors), releases))


if __name__ == "__main__":
    main()
