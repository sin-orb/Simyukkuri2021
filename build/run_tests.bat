@echo off
setlocal EnableExtensions EnableDelayedExpansion
set d=%CD%
cd /d "%~dp0.."

if exist bin rmdir /s /q bin
mkdir bin

dir /s /b src\main\java\*.java > sources.txt
javac -source 8 -target 8 -d bin -cp "lib\*" -encoding UTF-8 @sources.txt
if %ERRORLEVEL% NEQ 0 goto :fail

dir /s /b src\test\java\*.java > test_sources.txt
javac -source 8 -target 8 -d bin -cp "bin;lib\*" -encoding UTF-8 @test_sources.txt
if %ERRORLEVEL% NEQ 0 goto :fail

set "CP=bin;."
for %%J in (lib\*.jar) do set "CP=!CP!;%%~fJ"

java -Djava.awt.headless=true -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path "!CP!" --scan-class-path
if %ERRORLEVEL% NEQ 0 goto :fail

cd /d "%d%"
exit /b 0

:fail
set result=%ERRORLEVEL%
cd /d "%d%"
exit /b %result%
