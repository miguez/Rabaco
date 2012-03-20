@echo off
set BAK_CLASSPATH=%CLASSPATH%
set MIPATH=../../lib
set FILEPATH=../..

copy ..\..\webServicesConfiguration.properties .
copy ..\..\securityConfiguration.properties .
copy ..\..\configuration.properties .
copy ..\..\parametros.properties .

set CLASSPATH=
set CLASSPATH=%CLASSPATH%;%MIPATH%/mail-1.4.jar;%MIPATH%/activation.jar;%MIPATH%/addressing-1.0.jar;%MIPATH%/axis-1.4.jar;%MIPATH%/axis-jaxrpc-1.4.jar;%MIPATH%/axis-saaj-1.4.jar;%MIPATH%/bcprov-jdk13-132.jar;%MIPATH%/bcprov-jdk15-132.jar;%MIPATH%/commons-codec-1.3.jar;%MIPATH%/commons-discovery-0.2.jar;%MIPATH%/commons-httpclient-3.0.rc2.jar;%MIPATH%/commons-logging-1.0.4.jar;%MIPATH%/jaxrpc.jar;%MIPATH%/opensaml-1.0.1.jar;%MIPATH%/packager.jar;%MIPATH%/policy.jar;%MIPATH%/saaj.jar;%MIPATH%/wsclient.jar;%MIPATH%/wsdl4j-1.5.1.jar;%MIPATH%/wss4j-1.5.0.jar;%MIPATH%/xmlsec-1.3.0.jar;%MIPATH%/endorsed/resolver.jar;%MIPATH%/endorsed/xalan-2.7.0.jar;%MIPATH%/endorsed/serializer-2.7.0.jar;%MIPATH%/endorsed/xercesImpl.jar;%MIPATH%/endorsed/xml-apis.jar;%MIPATH%/jdic.jar;%MIPATH%/jdic.dll;%MIPATH%/jdic_stub.jar;%MIPATH%/JDigiDoc-2.1.6.jar;%MIPATH%/jnlp2msi.exe;%MIPATH%/MozEmbed.exe;%MIPATH%/netscape.jar;%MIPATH%/tray.dll;%MIPATH%/WinMsiWrapper.dll;%MIPATH%/xades-plugin.jar;%MIPATH%/IeEmbed.exe;%MIPATH%/jss33.jar;%MIPATH%/afirma5d0Commons1d0.jar;%MIPATH%/iaik_cms_ae.jar;%MIPATH%/iaik_jce_full_ae.jar;%MIPATH%/jce-jdk13-133.jar;%MIPATH%/xalan.jar;%MIPATH%/xercesImpl.jar;%MIPATH%/xml-apis.jar;%MIPATH%/xmlsec-1.3.0.jar;%MIPATH%/clienteFirmaAFirma5.jar
java -Djava.endorsed.dirs=%MIPATH%/endorsed com.telventi.afirma.wsclient.dss.custody.DSSArchiveSubmit %*

del webServicesConfiguration.properties
del securityConfiguration.properties
del configuration.properties
del parametros.properties

set CLASSPATH=%BAK_CLASSPATH%