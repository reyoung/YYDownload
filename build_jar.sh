#!/bin/bash
mvn clean compile package
cd YYMain
mvn assembly:single
mv target/*with-dependencies.jar ../YYMain.jar
cd ..
chmod a+x YYMain.jar

