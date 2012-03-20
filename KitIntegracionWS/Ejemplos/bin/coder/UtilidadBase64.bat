@echo off
set BAK_CLASSPATH=%CLASSPATH%
set MIPATH=../../lib

copy ..\..\configuration.properties .
copy ..\..\parametros.properties .

set CLASSPATH=
set CLASSPATH=%CLASSPATH%;%MIPATH%/wsclient.jar
java com.telventi.afirma.wsclient.coder.Base64CoderUtility %*

del configuration.properties
del parametros.properties

set CLASSPATH=%BAK_CLASSPATH%