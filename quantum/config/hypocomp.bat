@echo off
call setjava.bat
%JAVABIN% -classpath quantum.jar quantum.comparator.Hypo %*
