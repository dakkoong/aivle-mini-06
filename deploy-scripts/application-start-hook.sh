#!/bin/bash
set -e

APP_DIR=/opt/aivle-mini-06/backend
APP_JAR=$APP_DIR/bookapp-0.0.1-SNAPSHOT.jar
LOG_FILE=$APP_DIR/app.log

cat >/etc/nginx/conf.d/aivle-mini-06.conf <<'NGINX'
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /users {
        proxy_pass http://127.0.0.1:8080/users;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /books {
        proxy_pass http://127.0.0.1:8080/books;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
NGINX

nohup java -jar "$APP_JAR" > "$LOG_FILE" 2>&1 &
echo $! > "$APP_DIR/app.pid"

if command -v systemctl >/dev/null 2>&1; then
  systemctl restart nginx
else
  service nginx restart
fi
