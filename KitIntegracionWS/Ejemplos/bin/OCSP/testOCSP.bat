@echo off
set BAK_CLASSPATH=%CLASSPATH%
set MIPATH=../../lib
set FILEPATH=../..

copy ..\..\OCSPConfiguration.properties .
copy ..\..\parametros.properties .
copy ..\..\configuration.properties .

set CLASSPATH=
set CLASSPATH=%CLASSPATH%;
set CLASSPATH=%CLASSPATH%;%MIPATH%/endorsed/xalan.jar;%MIPATH%/activation.jar;%MIPATH%/axis.jar;%MIPATH%/axis-ant.jar;%MIPATH%/axis-schema.jar;%MIPATH%/bcmail-jdk14-116.jar;%MIPATH%/bcprov-jdk13-132.jar;%MIPATH%/clienteTSA.jar;%MIPATH%/commons-discovery.jar;%MIPATH%/commons-discovery-0.2.jar;%MIPATH%/commons-httpclient-3.0-rc2.jar;%MIPATH%/commons-logging.jar;%MIPATH%/commons-logging-1.0.4.jar;%MIPATH%/Excepcion.jar;%MIPATH%/iaik_cms.jar;%MIPATH%/iaik_jce_full_signed.jar;%MIPATH%/iaik_tsp.jar;%MIPATH%/javax.servlet.jar;%MIPATH%/jaxrpc.jar;%MIPATH%/junit.jar;%MIPATH%/log4j.properties;%MIPATH%/log4j-1.2.7.jar;%MIPATH%/log4j-1.2.8.jar;%MIPATH%/saaj.jar;%MIPATH%/wsdl4j-1.5.1.jar;%MIPATH%/xercesImpl.jar;%MIPATH%/xercesImpl-2.6.2.jar;%MIPATH%/xml-apis.jar;%MIPATH%/xml-apis-2.6.2.jar;%MIPATH%/xmlsec.jar;%MIPATH%/wsclient.jar;%MIPATH%/iaik_jce_full_314.jar
java -Djava.endorsed.dirs=%MIPATH%/endorsed com.telventi.afirma.OCSPTester.OCSPClient %*

del OCSPConfiguration.properties
del parametros.properties
del configuration.properties

set CLASSPATH=%BAK_CLASSPATH%