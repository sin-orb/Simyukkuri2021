@echo off
echo コンパイルを開始します。
javac -sourcepath src -d classes src\SimYukkuri.java
echo コンパイルが完了しました。続けて、jarファイルを作成します。
jar cvfm SimYukkuri.jar META-INF\MANIFEST.MF -C classes .
echo jarファイルの作成が完了しました。
pause

