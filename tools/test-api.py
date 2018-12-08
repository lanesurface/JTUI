#!/usr/bin/python3

"""
This script runs all tests against this API. If any tests fail, the error code
is returned.
"""

import os, sys
import re, argparse
import subprocess

root_dir = os.path.join(os.path.abspath(os.path.dirname(__file__)), '..')

# All tests must reside in the path '../src/test'. Files in this package
# directory will be built automatically and verified against the API.
test_path = os.sep.join([root_dir,
                         'src',
                         'test'])
tests = [name for _, _, name in os.walk(test_path)][0]

# The directories that will be used when building the classpath.
include_dirs = [os.path.join(root_dir, include) for include in
                ['res',
                 'src']]

package = 'test/'
package_name = re.sub('[/\\\\]', '.', package)

classpath = ['.', root_dir]
classpath.extend(include_dirs)

print(classpath)

# Verify all tests pass before creating a project. If the library is
# configured incorrectly, this will catch it before it causes confusion when
# trying to build a project later on down the road.
for test in tests:
  try:
    if not os.path.exists('../build'):
      os.mkdir('../build')

    paths = ';'.join(classpath)

    os.chdir('../src')
    subprocess.call(['javac',
                     '-d',
                     '../build',
                     '-cp',
                     paths,
                     package + test])

    os.chdir('../build')
    exit_code = subprocess.call(['java',
                                 '-cp',
                                 paths,
                                 package_name + test.split('.')[0]])
    if exit_code != 0:
      sys.stderr.write('Could not run the test {}.'.format(test))
      sys.exit(exit_code)
  except FileNotFoundError:
      print('You must specify the path to your JDK with the flag --JDK_BIN \
<path> or add the directory to your system\'s PATH before proceeding.')
