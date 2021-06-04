@echo off
set d=%CD%
cd %~dp0
md classes 2> NUL
echo Start compiling...
javac -encoding UTF-8 -sourcepath .. -d classes ..\src\SimYukkuri.java
echo Compile completed; packing SimYukkuri.jar file...
jar cvfe SimYukkuri.jar src.SimYukkuri -C classes . -C .. data -C .. images -C .. resources
echo Packing completed
cd %d%
pause

