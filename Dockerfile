# JDK11 이미지 사용
FROM openjdk:11-jdk

# VOLUME 설정
VOLUME /tmp

# Jar파일 위치 변수 정의
ARG JAR_FILE="build/libs/*.jar"

# Host에 있는 build파일을 Docker 컨테이너에 app.jar로 복사
COPY ${JAR_FILE} app.jar

# application-prod.yml을 사용하기 위한 환경 변수 정의
ENV PROFILE prod

# 도커 이미지가 run 될 시에 수행할 동작
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar","/app.jar"]