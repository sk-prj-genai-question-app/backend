# Stage 1: 빌드 환경 설정 (Gradle을 사용하여 JAR 파일 생성)
# Gradle 8.7과 JDK 17이 포함된 Alpine Linux 기반 이미지 사용 (가볍고 효율적)
FROM gradle:8.7-jdk17-alpine AS builder

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /app

# 프로젝트의 모든 파일(소스 코드, build.gradle 등)을 컨테이너의 /app 디렉토리로 복사
# dockerignore 파일이 있다면, 해당 파일에 명시된 내용은 복사되지 않습니다.
COPY . .

# Gradle을 사용하여 프로젝트 빌드
# 'clean build' 명령을 실행하여 프로젝트를 클린하고 빌드합니다.
# GitHub Actions에서 이미 테스트를 수행하므로, 여기서는 -x test 옵션으로 테스트를 제외하여 빌드 시간을 절약합니다.
RUN gradle clean build -x test

# Stage 2: 런타임 환경 설정 (빌드된 JAR 파일을 실행)
# OpenJDK 17의 슬림(slim) 버전 이미지 사용 (필수 런타임만 포함되어 이미지 크기가 작음)
FROM openjdk:17-jdk-slim

# Spring Boot 애플리케이션이 임시 파일을 저장할 수 있도록 /tmp 볼륨을 설정합니다.
# 컨테이너 재시작 시 임시 파일이 사라지도록 하여 컨테이너의 상태를 유지하지 않게 합니다.
VOLUME /tmp

# 빌더 스테이지에서 생성된 JAR 파일을 최종 이미지로 복사합니다.
# 'GenAiQuestionApp-0.0.1-SNAPSHOT.jar' 파일을 'app.jar'라는 이름으로 복사합니다.
# 'app.jar'는 컨테이너 내에서 실행될 애플리케이션의 이름입니다.
COPY --from=builder /app/build/libs/GenAiQuestionApp-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너가 시작될 때 실행될 명령어 정의
# 'java -jar app.jar' 명령으로 Spring Boot 애플리케이션을 실행합니다.
# '--spring.profiles.active=prod'는 Spring 프로파일을 'prod'로 설정하여 프로덕션 환경 설정을 활성화합니다.
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]

# 애플리케이션이 사용할 포트를 외부에 노출합니다.
# Spring Boot의 기본 포트는 8080입니다. 애플리케이션 설정에 따라 변경될 수 있습니다.
EXPOSE 8080
