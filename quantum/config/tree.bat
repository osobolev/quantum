@echo off
call setjava.bat
%JAVABIN% -Xmx1024m -classpath quantum.jar quantum.qtree.AnimationFrame %*
