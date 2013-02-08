#!/bin/bash
echo -e -n "1.) Pleased download the Oracle JDBC Driver ojdbc6.jar\ fron: \n"
echo -e -n "http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html \n"
echo -e -n "2.) Pleased download the Oracle JPublicher \n"
echo -e -n "http://download.oracle.com/otn/utilities_drivers/jdbc/10201/jpub_102.zip \n"
echo -e -n "3.) Put the files ojdbc6.jar, runtime12.jar and translator.jar in the current folder."
echo -e -n "4.) Run the script again."

mvn install:install-file \
-DgroupId=Oracle \
-DartifactId=ojdbc6 \
-Dpackaging=jar \
-Dversion=6.0 \
-Dfile=ojdbc6.jar \
-DgeneratePom=true

mvn install:install-file \
-DgroupId=Oracle \
-DartifactId=runtime12 \
-Dpackaging=jar \
-Dversion=12 \
-Dfile=runtime12.jar \
-DgeneratePom=true

mvn install:install-file \
-DgroupId=Oracle \
-DartifactId=translator \
-Dpackaging=jar \
-Dversion=1.0 \
-Dfile=translator.jar \
-DgeneratePom=true
