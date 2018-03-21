#!/bin/bash
nohup java -jar message-0.0.1-SNAPSHOT.jar --spring.profiles.active=myown --gitlab.config-enable.sonar-check=true &
# 当前运行的pid
#echo $! > message.pid
