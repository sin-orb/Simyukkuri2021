#!/usr/bin/env sh
set -eu

cd "$(dirname "$0")/.."

rm -rf bin bin2 build/test-classes
mkdir -p bin

find src/main/java -name '*.java' | sort > /tmp/simyukkuri_sources.txt
javac -source 8 -target 8 -d bin -cp "lib/*" -encoding UTF-8 @/tmp/simyukkuri_sources.txt

find src/test/java -name '*.java' | sort > /tmp/simyukkuri_tests.txt
javac -source 8 -target 8 -d bin -cp "bin:lib/*" -encoding UTF-8 @/tmp/simyukkuri_tests.txt

CP="bin:."
for jar in lib/*.jar; do
  CP="$CP:$jar"
done

java -Djava.awt.headless=true \
  -jar lib/junit-platform-console-standalone-1.10.2.jar \
  execute \
  --class-path "$CP" \
  --scan-class-path
