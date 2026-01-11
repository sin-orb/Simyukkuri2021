@echo off
setlocal
set BASE=%~dp0
set CP=%BASE%SimYukkuri.jar;%BASE%..\\lib\\*
"java.exe" -Xms1024m -Xmx3072m -cp "%CP%" src.SimYukkuri
