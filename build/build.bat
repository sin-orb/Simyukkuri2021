@echo off
set d=%CD%
cd %~dp0
md classes 2> NUL
echo Start compiling...
javac -encoding UTF-8 -sourcepath .. -d classes ..\src\SimYukkuri.java
echo Compile completed.Then making SimYukkuri.jar file...
jar cvfe SimYukkuri.jar src.SimYukkuri -C classes . -C .. data -C .. images
echo SimYukkuri.jar file have made.
cd %d%
pause

