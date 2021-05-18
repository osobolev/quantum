@echo off
call setjava.bat
%JAVABIN% -Xmx512m -classpath quantum.jar quantum.draw.ShowFrame %*
