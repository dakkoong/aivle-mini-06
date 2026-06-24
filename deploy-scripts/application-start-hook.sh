#!/bin/bash
echo "=== 배포 스크립트 시작 ==="

# 1. 기존에 실행 중인 백엔드(Java) 프로세스가 있다면 종료 (8080 포트 기준)
CURRENT_PID=$(lsof -t -i:8080)
if [ -z "$CURRENT_PID" ]; then
    echo "현재 실행 중인 백엔드 프로세스가 없습니다."
else
    echo "기존 백엔드 프로세스 종료 중 (PID: $CURRENT_PID)..."
    kill -15 $CURRENT_PID
    sleep 5
fi

# 2. 백엔드 JAR 파일이 배포된 위치로 이동
# CodeDeploy는 기본적으로 아래 경로의 'deployment-archive' 폴더에 압축을 풉니다.
cd /opt/codedeploy-agent/deployment-root/$DEPLOYMENT_GROUP_ID/$DEPLOYMENT_ID/deployment-archive/backend/build/libs/

# 3. 백엔드 서버 실행 (로그를 backend.log에 남기고 백그라운드 실행)
echo "새로운 백엔드 스프링부트 서버 실행..."
nohup java -jar *.jar > backend.log 2>&1 &
sudo service nginx restart