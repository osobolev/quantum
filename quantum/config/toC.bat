@echo off
call setjava.bat
%JAVABIN% -classpath quantum.jar common.graph.model.ConvertToC %*
