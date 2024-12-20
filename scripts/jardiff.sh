#!/bin/bash -x

# https://stackoverflow.com/questions/2870992/automatic-exit-from-bash-shell-script-on-error
set -beh -o pipefail

if [ $* -ne 2 ]; then
  echo "useage: $0 jar_gradle_spec1 jar_gradle_spec2"
  exit -1
fi
export JAR1="$1"
shift
export JAR2="$2"
shift

export GRADLE=./gradlew

export GIT_ROOT=`git rev-parse --show-toplevel`
pushd "$GIT_ROOT"

$GRADLE deleteJars
cp build.gradle.kts build.gradle.kts2
sed -i -E -e "s/downloadJar1.*$/downloadJar1\(\"$JAR1\"\)/" build.gradle.kts
sed -i -E -e "s/downloadJar2.*$/downloadJar1\(\"$JAR1\"\)/" build.gradle.kts
$GRADLE build -x test
# mv build.gradle.kts2 build.gradle.kts

popd
