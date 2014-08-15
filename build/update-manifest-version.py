#!/bin/env dls-python

# Typical usage:
# find core/plugins/*/ -maxdepth 3 -name pom.xml -exec ./update.py {} \;
import sys

LABEL = 'Bundle-Version: '

def increment(line):
    versions = line.split()[1].split('.')
    versions[2] = '%d' % (int(versions[2]) + 1)
    return LABEL + '.'.join(versions) + '\n'

if __name__ == '__main__':
    if not sys.argv[1:]:
        print 'Takes a valid manifest file as argument'
        sys.exit(-1)

    file = sys.argv[1]
    lines = [line for line in open(file)]
    lines = [increment(l) if LABEL in l else l for l in lines]

    out = open(file, 'w')
    for line in lines:
        out.write(line)
