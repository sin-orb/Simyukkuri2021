#!/usr/bin/env sh
# Simyukkuri セーブデータエディター 起動スクリプト
set -eu

cd "$(dirname "$0")/.."

# ── ソースコンパイル ──────────────────────────────────────────
mkdir -p bin

find src/main/java -name '*.java' | sort > /tmp/simyukkuri_editor_sources.txt
javac --release 8 -Xlint:-options -d bin -cp "lib/*" -encoding UTF-8 @/tmp/simyukkuri_editor_sources.txt

# ── クラスパス構築 ─────────────────────────────────────────────
CP="bin:."
for jar in lib/*.jar; do
  CP="$CP:$jar"
done

# ── 起動 ───────────────────────────────────────────────────────
# カレントディレクトリをプロジェクトルートにすることで
# ResourceUtil が ./resources/ からプロパティファイルを読める
java -cp "$CP" org.simyukkuri.editor.SaveEditor "$@"
