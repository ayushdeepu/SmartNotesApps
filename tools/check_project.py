#!/usr/bin/env python3
"""Stdlib-only static validation. Full Android compilation is done by Gradle."""
from pathlib import Path
from xml.etree import ElementTree as ET
import re
import subprocess
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'app/src/main/res'
JAVA = ROOT / 'app/src/main/java/com/ayush/smartnotes'

required = [
    'settings.gradle', 'build.gradle', 'app/build.gradle', 'gradlew',
    'gradlew.bat', 'gradle/wrapper/gradle-wrapper.jar',
    'gradle/wrapper/gradle-wrapper.properties',
    'app/src/main/AndroidManifest.xml',
    'app/src/main/res/layout/activity_main.xml',
    'app/src/main/res/layout/activity_editor.xml',
    'app/src/main/res/layout/item_note.xml',
    'app/src/main/java/com/ayush/smartnotes/MainActivity.java',
    'app/src/main/java/com/ayush/smartnotes/EditorActivity.java',
    'app/src/main/java/com/ayush/smartnotes/data/NotesDbHelper.java',
    'app/src/main/java/com/ayush/smartnotes/data/Note.java',
    'app/src/main/java/com/ayush/smartnotes/ui/NoteAdapter.java',
    'app/src/main/java/com/ayush/smartnotes/util/SmartText.java',
]
missing = [p for p in required if not (ROOT / p).is_file()]
if missing:
    sys.exit('Missing required files:\n' + '\n'.join(missing))

with zipfile.ZipFile(ROOT / 'gradle/wrapper/gradle-wrapper.jar') as jar:
    assert jar.testzip() is None
    assert 'org/gradle/wrapper/GradleWrapperMain.class' in jar.namelist(), 'Invalid wrapper jar'

xmls = list((ROOT / 'app/src/main').rglob('*.xml'))
for p in xmls:
    ET.parse(p)

# All @+id declarations and @id usages referenced in Java must exist.
layout_text = '\n'.join(p.read_text() for p in RES.rglob('*.xml'))
ids = set(re.findall(r'@\+id/([A-Za-z_][A-Za-z0-9_]*)', layout_text))
java_sources = list(JAVA.rglob('*.java'))
for p in java_sources:
    code = p.read_text()
    assert code.startswith('package com.ayush.smartnotes'), f'Wrong Java package in {p}'
    refs = set(re.findall(r'R\.id\.([A-Za-z_][A-Za-z0-9_]*)', code))
    assert not (refs - ids), f'Unknown IDs in {p.name}: {sorted(refs - ids)}'

xml_refs = re.findall(r'@(?:(?:drawable|color|layout|string|mipmap|style)/)([A-Za-z_][A-Za-z0-9_]*)', layout_text)
assert len(xml_refs) > 0
assert not list(ROOT.rglob('*.kt')), 'Unexpected Kotlin files'
assert 'android.yml' in '\n'.join(str(p) for p in ROOT.rglob('android.yml'))
print(f'PASS: {len(xmls)} valid XMLs, {len(java_sources)} Java files, no Kotlin, Gradle wrapper present.')
