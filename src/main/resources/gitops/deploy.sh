#
# Copyright 2020 XEBIALABS
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

set -x
REPOSITORY='https://github.com/bmoussaud/petclinic-gitops.git'
BRANCH='master'
LAST_KNOWN_COMMIT='1a208bac0ffba20e464061acc790bed1338ee09'
YAML_PATH='petclinic.yaml'

LAST_COMMIT=`git ls-remote $REPOSITORY refs/heads/$BRANCH |  awk '{ print $1 }' `
echo $LAST_COMMIT
if [ "$LAST_COMMIT" = "$LAST_KNOWN_COMMIT" ]; then
    echo "Same commit id... exit" 
    exit 0
fi

echo 'New commit id'
git clone $REPOSITORY $LAST_COMMIT
cd $LAST_COMMIT
xl apply -f $YAML_PATH



