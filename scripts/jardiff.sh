#!/bin/bash -x

# https://stackoverflow.com/questions/2870992/automatic-exit-from-bash-shell-script-on-error
set -beh -o pipefail

if [ $* -ne 2 ]; then
  echo "useage: $0 jar_gradle_spec1 jar_gradle_spec2"
  exit -1
fi
export JAR1="$1"
shift
export JAR2="$1"
shift
export JAR_BASENAME_1=`echo "$JAR1" | cut -d ':' -f 2`
export JAR_BASENAME_2=`echo "$JAR2" | cut -d ':' -f 2`

export GRADLE=./gradlew

export GIT_ROOT=`git rev-parse --show-toplevel`
pushd "$GIT_ROOT"

$GRADLE deleteJars
cp build.gradle.kts build.gradle.kts2
sed -i -E -e 's/^[ ]*downloadJar1(.*$/    downloadJar1("'$JAR1'")/' build.gradle.kts
sed -i -E -e 's/^[ ]*downloadJar2(.*$/    downloadJar2("'$JAR2'")/' build.gradle.kts
$GRADLE build -x test

export JAR_FILE_1=`ls build/test-jars-1/${JAR_BASENAME_1}*.jar`
export JAR_FILE_2=`ls build/test-jars-2/${JAR_BASENAME_2}*.jar`

$GRADLE run --args "$JAR_FILE_1 $JAR_FILE_2"
mv build.gradle.kts2 build.gradle.kts

popd
