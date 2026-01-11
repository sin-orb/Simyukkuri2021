@echo off
setlocal
set d=%CD%
cd %~dp0
md classes 2> NUL
md classes_lib 2> NUL
set CP=..\lib\jackson-annotations-2.9.9.jar;..\lib\jackson-core-2.9.9.jar;..\lib\jackson-databind-2.9.9.jar
set CLASSPATH=%CLASSPATH%;%CP%
echo Start compiling...
javac -encoding UTF-8 -sourcepath .. -d classes ..\src\SimYukkuri.java
echo Extracting libraries...
pushd classes_lib
for %%J in ("%~dp0..\lib\jackson-annotations-2.9.9.jar" "%~dp0..\lib\jackson-core-2.9.9.jar" "%~dp0..\lib\jackson-databind-2.9.9.jar") do jar xf "%%~fJ"
popd
if exist classes_lib\META-INF\*.SF del /q classes_lib\META-INF\*.SF
if exist classes_lib\META-INF\*.DSA del /q classes_lib\META-INF\*.DSA
if exist classes_lib\META-INF\*.RSA del /q classes_lib\META-INF\*.RSA
echo Compile completed; packing SimYukkuri.jar file...
jar cvfe SimYukkuri.jar src.SimYukkuri -C classes . -C classes_lib . -C .. data -C .. images -C .. resources -C .. lib
echo Packing completed
cd %d%
pause

