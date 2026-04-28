@echo off
setlocal
set d=%CD%
cd /d "%~dp0.."

if exist bin rmdir /s /q bin
mkdir bin

dir /s /b src\*.java > sources.txt
javac -d bin -cp "lib\*" -encoding UTF-8 @sources.txt
if %ERRORLEVEL% NEQ 0 goto :fail

dir /s /b test\src\*.java > test_sources.txt
javac -d bin -cp "bin;lib\*" -encoding UTF-8 @test_sources.txt
if %ERRORLEVEL% NEQ 0 goto :fail

java -Djava.awt.headless=true -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path "bin;lib\*" --scan-class-path
if %ERRORLEVEL% NEQ 0 goto :fail

cd /d "%d%"
exit /b 0

:fail
set result=%ERRORLEVEL%
cd /d "%d%"
exit /b %result%
