# Build And Test

## 前提

- ターゲットランタイムは Java 8 のまま維持する。
- コンパイルも Java 8 互換を前提にする。
- 現行リポジトリは Maven/Gradle を使っていない。
- 依存 jar は `lib/` に置く。
- コンパイル出力先は `bin/`。

主な依存:

- Jackson 2.9.9
- JUnit Platform Console Standalone 1.10.2
- JUnit Jupiter API 5.10.2
- opentest4j 1.3.0
- apiguardian-api 1.1.2

依存 jar の管理方針:

- Jackson 2.9.9 の 3 jar はリポジトリで追跡している。
  - `lib/jackson-annotations-2.9.9.jar`
  - `lib/jackson-core-2.9.9.jar`
  - `lib/jackson-databind-2.9.9.jar`
- JUnit Console standalone は GitHub Actions で Maven Central から取得する。
- ローカルの JUnit/Jacoco/opentest4j/apiguardian jar は補助ファイル扱いで Git 管理しない。
- 現行 CI は Jacoco を使っていない。

## Windows: 配布 jar ビルド

通常のアプリ配布物を作る Windows ビルドは `build/build.bat`。

```bat
build\build.bat
```

主な処理:

- `build/classes` へアプリ本体をコンパイル
- Jackson jar を `build/classes_lib` に展開
- `data`, `images`, `resources`, `lib` を含めて `build/SimYukkuri.jar` を作成

この手順はアプリ配布用であり、JUnit テストクラスはコンパイルしない。

## Linux/macOS: 配布 jar ビルド

Linux/macOS では `build/build.sh` を使う。

```sh
build/build.sh
```

主な処理は Windows の `build/build.bat` と同じ。

- `build/classes` へアプリ本体をコンパイル
- Jackson jar を `build/classes_lib` に展開
- `data`, `images`, `resources`, `lib` を含めて `build/SimYukkuri.jar` を作成

## 起動スクリプト

Windows:

```bat
build\start_2GB.bat
build\start_3GB.bat
build\start_4GB.bat
build\start_4GB_en_US.bat
build\start_8GB.bat
```

Linux/macOS:

```sh
build/start_2GB.sh
build/start_3GB.sh
build/start_4GB.sh
build/start_4GB_en_US.sh
build/start_8GB.sh
```

各スクリプトは `build/SimYukkuri.jar` と `lib/*` を classpath に入れて `src.SimYukkuri` を起動する。

| スクリプト | JVM オプション |
| --- | --- |
| `start_2GB.*` | `-Xms1024m -Xmx2048m` |
| `start_3GB.*` | `-Xms1024m -Xmx3072m` |
| `start_4GB.*` | `-Xms1024m -Xmx4096m` |
| `start_4GB_en_US.*` | `-Duser.language=en -Duser.country=US -Xms1024m -Xmx4096m` |
| `start_8GB.*` | `-Xms1024m -Xmx8192m` |

## Windows: テスト実行

ルートから実行する。

```bat
build\run_tests.bat
```

内容:

```bat
cd /d "%~dp0.."
if exist bin rmdir /s /q bin
mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin -cp "lib\*" -encoding UTF-8 @sources.txt
dir /s /b test\src\*.java > test_sources.txt
javac -d bin -cp "bin;lib\*" -encoding UTF-8 @test_sources.txt
java -Djava.awt.headless=true -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path "bin;lib\*" --scan-class-path
```

注意:

- `sources.txt` と `test_sources.txt` は作業ファイルとして生成される。
- `bin/` は生成物なので Git 管理しない。
- この手順はテスト実行用で、配布 jar は作らない。
- コンパイルに失敗した場合は JUnit を実行しない。

## Linux/macOS: テスト実行

ルートから実行する。

```sh
build/run_tests.sh
```

内容:

```sh
rm -rf bin
mkdir -p bin
find src -name '*.java' | sort > /tmp/simyukkuri_sources.txt
javac -d bin -cp "lib/*" -encoding UTF-8 @/tmp/simyukkuri_sources.txt
find test -name '*.java' | sort > /tmp/simyukkuri_tests.txt
javac -d bin -cp "bin:lib/*" -encoding UTF-8 @/tmp/simyukkuri_tests.txt
CP="bin"
for jar in lib/*.jar; do
  CP="$CP:$jar"
done
java -Djava.awt.headless=true -jar lib/junit-platform-console-standalone-1.10.2.jar execute --class-path "$CP" --scan-class-path
```

Java 8 互換を明示して確認したい場合:

```sh
find src -name '*.java' | sort > /tmp/simyukkuri_sources.txt
find test -name '*.java' | sort > /tmp/simyukkuri_tests.txt
javac -source 8 -target 8 -d bin -cp "lib/*" -encoding UTF-8 @/tmp/simyukkuri_sources.txt
javac -source 8 -target 8 -d bin -cp "bin:lib/*" -encoding UTF-8 @/tmp/simyukkuri_tests.txt
```

## JUnit 実行

JUnit は `lib/junit-platform-console-standalone-1.10.2.jar` で実行する。通常は `build/run_tests.bat` または `build/run_tests.sh` を使う。

Linux/macOS では、JUnit Console の `--class-path "bin:lib/*"` が環境によって期待通り展開されないことがある。安定させるには classpath を明示的に組む。

```sh
CP="bin"
for j in lib/*.jar; do
  CP="$CP:$j"
done

java -Djava.awt.headless=true \
  -jar lib/junit-platform-console-standalone-1.10.2.jar \
  execute \
  --class-path "$CP" \
  --scan-class-path
```

Windows の場合:

```bat
java -Djava.awt.headless=true -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path "bin;lib\*" --scan-class-path
```

## パッケージ単位テスト

例: `src.yukkuri` のみ。

```sh
CP="bin"
for j in lib/*.jar; do
  CP="$CP:$j"
done

java -Djava.awt.headless=true \
  -jar lib/junit-platform-console-standalone-1.10.2.jar \
  execute \
  --class-path "$CP" \
  --scan-class-path \
  --include-package src.yukkuri
```

例: `src.logic` のみ。

```sh
java -Djava.awt.headless=true \
  -jar lib/junit-platform-console-standalone-1.10.2.jar \
  execute \
  --class-path "$CP" \
  --scan-class-path \
  --include-package src.logic
```

## 単一テストクラス

```sh
java -Djava.awt.headless=true \
  -jar lib/junit-platform-console-standalone-1.10.2.jar \
  execute \
  --class-path "$CP" \
  --select-class src.yukkuri.DeibuTest
```

## 単一テストメソッド

```sh
java -Djava.awt.headless=true \
  -jar lib/junit-platform-console-standalone-1.10.2.jar \
  execute \
  --class-path "$CP" \
  --select-method src.yukkuri.DeibuTest#testDeibuIsImageLoaded
```

複数メソッドを指定する例:

```sh
java -Djava.awt.headless=true \
  -jar lib/junit-platform-console-standalone-1.10.2.jar \
  execute \
  --class-path "$CP" \
  --select-method src.yukkuri.DeibuTest#testDeibuIsImageLoaded \
  --select-method src.yukkuri.DosMarisaTest#testDosMarisaIsImageLoaded
```

## 既知の注意点

- GUI/Swing に触るテストは headless 実行で失敗することがある。
- `MainCommandUI` など AWT 初期化に触る経路は X11 の有無に影響される。
- `SimYukkuri.RND`, `SimYukkuri.world`, 画像ロード済みフラグなど static 状態を使うテストは順序依存になりやすい。
- yukkuri 画像ロード状態はクラスごとの static boolean なので、`isImageLoaded()` の戻り値を未ロード前提で固定しない。
- 全テスト実行前には `bin/` を消して再コンパイルする。
