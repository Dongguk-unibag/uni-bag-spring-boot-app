FROM openjdk:17-jdk-slim

WORKDIR /app

ARG JAR_FILE=./build/libs/uni_bag_spring_boot_app-0.0.1-SNAPSHOT.jar
ARG PROFILES
ARG ENV

# JAR 파일 메인 디렉토리에 복사
COPY ${JAR_FILE} uni-bag-spring-boot-app.jar

# 정적 파일을 저장하기 위한 공간
RUN mkdir -p /app/mm
RUN mkdir -p /app/mm/images

# 시스템 진입점 정의
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=${PROFILES}", "-jar", "uni-bag-spring-boot-app.jar"]