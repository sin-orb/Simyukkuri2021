#!/usr/bin/env sh
set -eu

cd "$(dirname "$0")"

mkdir -p classes classes_lib

CP="../lib/jackson-annotations-2.9.9.jar:../lib/jackson-core-2.9.9.jar:../lib/jackson-databind-2.9.9.jar"

echo "Start compiling..."
javac -source 8 -target 8 -encoding UTF-8 -cp "$CP" -sourcepath ../src/main/java -d classes ../src/main/java/org/simyukkuri/SimYukkuri.java

echo "Extracting libraries..."
(
  cd classes_lib
  jar xf ../../lib/jackson-annotations-2.9.9.jar
  jar xf ../../lib/jackson-core-2.9.9.jar
  jar xf ../../lib/jackson-databind-2.9.9.jar
)

rm -f classes_lib/META-INF/*.SF classes_lib/META-INF/*.DSA classes_lib/META-INF/*.RSA

echo "Compile completed; packing SimYukkuri.jar file..."
jar cfe SimYukkuri.jar org.simyukkuri.SimYukkuri -C classes . -C classes_lib . -C .. data -C .. images -C .. resources -C .. lib
echo "Packing completed"
