@echo off
call setjava.bat
%JAVABIN% -Xmx512m -classpath neuron.jar neuron.draw.GraphFrame %*
