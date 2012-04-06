@echo off
IF NOT DEFINED STRIPESGEN_HOME GOTO NO_VAR_DEF
java -cp "%STRIPESGEN_HOME%\lib\groovy-all-1.7.4.jar;%STRIPESGEN_HOME%\lib\stripes-generator-1.0.jar" org.abnt.stripes.generator.StripesGenerator %*
GOTO END
:NO_VAR_DEF
ECHO the STRIPESGEN_HOME environment variable must be set
:END