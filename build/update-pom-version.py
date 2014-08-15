#!/bin/env dls-python

# Typical usage:
# find core/plugins/*/ -maxdepth 3 -name pom.xml -exec ./update.py {} \;
import sys
import xml.etree.ElementTree as ET

def increment(element):
    parts = element.text.split('-')
    num = parts[0].split('.')
    num[-1] = '%d' % (int(num[-1]) + 1)
    parts[0] = '.'.join(num)
    element.text = '-'.join(parts)

if __name__ == '__main__':
    if not sys.argv[1:]:
        print 'Takes a valid pom file as argument'
        sys.exit(-1)

    ET.register_namespace('', 'http://maven.apache.org/POM/4.0.0')
    pom = ET.parse(sys.argv[1])
    root = pom.getroot()
    [increment(r) for r in root.findall('{http://maven.apache.org/POM/4.0.0}version')]
    pom.write(sys.argv[1], xml_declaration=True,  encoding="utf-8")
