@echo off
set d=%CD%
cd %~dp0
md classes 2> NUL
set CLASSPATH=%CLASSPATH%;..\lib\jackson-annotations-2.9.9.jar;..\lib\jackson-core-2.9.9.jar;..\lib\jackson-databind-2.9.9.jar
echo Start compiling...
javac -encoding UTF-8 -sourcepath .. -d classes ..\src\SimYukkuri.java
echo Compile completed; packing SimYukkuri.jar file...
jar cvfe SimYukkuri.jar src.SimYukkuri -C classes . -C .. data -C .. images -C .. resources -C .. lib
echo Packing completed
cd %d%
pause

