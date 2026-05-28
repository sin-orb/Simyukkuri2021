@echo off
REM Simyukkuri セーブデータエディター 起動スクリプト

cd /d "%~dp0.."

REM ── ソースコンパイル ───────────────────────────────────────
if not exist bin mkdir bin

dir /s /b src\main\java\*.java > %TEMP%\simyukkuri_editor_sources.txt
javac -source 8 -target 8 -d bin -cp "lib\*" -encoding UTF-8 @%TEMP%\simyukkuri_editor_sources.txt
if errorlevel 1 (
    echo コンパイルに失敗しました。
    pause
    exit /b 1
)

REM ── クラスパス構築 ─────────────────────────────────────────
set CP=bin;.
for %%j in (lib\*.jar) do set CP=%CP%;%%j

REM ── 起動 ──────────────────────────────────────────────────
java -cp "%CP%" org.simyukkuri.editor.SaveEditor %*
if errorlevel 1 pause
