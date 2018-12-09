#!/usr/bin/python3

"""
This script runs all tests against this API. If any tests fail, the error code
is returned.
"""

import os, sys
import argparse
import subprocess

root_dir = os.path.join(os.path.abspath(os.path.dirname(__file__)),
                        os.pardir)

# The package in the source folder that holds the tests to run against the API.
# Classes in this package will be built automatically.
package = 'test'
test_path = os.sep.join([root_dir,
                         'src',
                         package])
tests = [name for _, _, name in os.walk(test_path)][0]

# The directories that will be used when building the classpath.
### APPARENTLY THE SRC FOLDER DOESN'T NEED TO BE INCLUDED, AND RES ONLY ###
###        NEEDS TO BE LINKED AT RUNTIME. FIX THIS AT SOME POINT.       ###
include_dirs = [os.path.join(root_dir, include) for include in
                ['res',
                 'src']]
include_dirs.append('.')
classpath = os.pathsep.join(include_dirs)

argument_parser = argparse.ArgumentParser(description='''\
Compile the library and run tests in "%s" against this API.''' % package)
argument_parser.add_argument('-jdk',
                             '--JDK_BIN',
                             default='',
                             metavar='path',
                             dest='jdk',
                             help='Use the path to this JDK to build the classes.')
jdk_path = argument_parser.parse_args().jdk
run_javac, run_java = [os.path.join(jdk_path, cmd) for cmd in ['javac', 'java']]

for test in tests:
  try:
    if not os.path.exists('../build'):
      os.mkdir('../build')

    os.chdir('../src')
    subprocess.call([run_javac,
                     '-d',
                     '../build',
                     '-cp',
                     classpath,
                     os.sep.join([package, test])])

    os.chdir('../build')
    exit_code = subprocess.call([run_java,
                                 '-cp',
                                 classpath,
                                 os.extsep.join([package,
                                                 test.split('.')[0]])])
    if exit_code != 0:
      sys.stderr.write('Could not run the test {}.'.format(test))
      sys.exit(exit_code)
  except FileNotFoundError:
      print('You must specify the path to your JDK with the flag --JDK_BIN \
<path> or add the directory to your system\'s PATH before proceeding.')
