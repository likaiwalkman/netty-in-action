#!/usr/bin/env bash
#nohup java -jar netty-client.jar > client.log 2>&1 &
mvn package assembly:single -P netty-server -Dmaven.test.skip=true
