#!/bin/bash
mvn clean compile package install
cd YYMain
mvn assembly:single
mv target/*with-dependencies.jar ../YYCli.jar
cd ..
chmod a+x YYCli.jar
cd YYVideoMerger
mvn assembly:single
mv target/*with-dependencies.jar ../YYVideoMerger.jar
cd ..
chmod a+x YYVideoMerger.jar

