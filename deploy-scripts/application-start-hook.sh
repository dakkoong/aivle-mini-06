#!/bin/bash

pkill -f '.jar'

JAR_NAME=$(ls /home/ec2-user/app/*.jar | tail -n 1)

nohup java -jar $JAR_NAME > /home/ec2-user/app/app.log 2>&1 &