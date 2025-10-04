[English](./README.md) | [한국어](./README.ko.md) | [日本語](./README.ja.md)

---

# 📚 JLPT 문제 생성 학습 도우미 - 백엔드

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/user/repo/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg](./LICENSE)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](#-tech-stack)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen.svg)](#-tech-stack)

"생성형 AI를 통한 JLPT 문제 생성 학습 도우미" 웹 애플리케이션의 백엔드 서버입니다. 사용자 관리, 인증, JLPT 문제 생성 및 기록 관리, AI 서비스 연동 등 핵심 비즈니스 로직을 담당합니다.

## ✨ 주요 기능

- **🔐 사용자 인증**: JWT(JSON Web Token) 기반의 안전한 로그인 및 회원가입 기능.
- **🤖 AI 문제 생성**: 생성형 AI 모델과 연동하여 JLPT 예상 문제를 동적으로 생성.
- **📖 문제 관리**: 생성된 문제를 조회, 수정, 삭제하는 RESTful API 제공.
- **📈 학습 기록 관리**: 사용자의 문제 풀이 이력 및 정답률을 추적하고 관리.
- **🗣️ AI 질의응답**: 사용자의 질문을 AI 모델에 전달하고 답변을 받아오는 기능.
- **👑 관리자 기능**: 사용자, 문제, 학습 기록 등을 관리할 수 있는 Thymeleaf 기반의 관리자 페이지.

## 🛠️ 기술 스택

| 구분 | 기술 | 버전 |
| :--- | :--- | :--- |
| **언어** | Java | 17 |
| **프레임워크** | Spring Boot | 3.4.7 |
| **빌드 도구** | Gradle | |
| **데이터베이스** | MariaDB (운영), H2 (테스트) | |
| **DB 마이그레이션** | Flyway | |
| **인증/보안** | Spring Security, JJWT | 0.11.5 |
| **ORM** | Spring Data JPA | |
| **템플릿 엔진** | Thymeleaf | |
| **기타** | Lombok, Actuator, Testcontainers | |

## 🏛️ 프로젝트 구조

```
src/main/java/com/rookies3/genaiquestionapp
├── GenAiQuestionAppApplication.java  # Spring Boot 메인 애플리케이션
├── admin/                  # 관리자 기능 관련 컨트롤러, 서비스
├── ai/                     # 외부 AI 서비스 연동 로직
├── auth/                   # 회원가입, 로그인 등 사용자 인증
├── config/                 # Security, CORS 등 전역 설정
├── entity/                 # JPA 엔티티 (데이터베이스 테이블 매핑)
├── exception/              # 전역 예외 처리
├── jwt/                    # JWT 생성, 검증 유틸리티
├── problem/                # 문제 생성, 조회, 관리
├── record/                 # 사용자 학습 기록 관리
├── userquestion/           # 사용자 질문 처리
└── util/                   # 공통 유틸리티 클래스
```

## 🚀 시작하기

### 1. 사전 요구사항

- Java 17
- Gradle
- Docker (선택 사항)

### 2. 설치 및 설정

1.  **저장소 클론**
    ```bash
    git clone https://github.com/your-username/jlpt-backend.git
    cd jlpt-backend
    ```

2.  **애플리케이션 설정 (Best Practice)**
    `src/main/resources/` 디렉토리에 `application-prod.yml` 파일을 생성하고, 실제 운영 환경에 맞게 아래 내용을 수정합니다. 로컬 개발 시에는 `application-dev.yml`을 생성하여 사용하세요.

    ```yaml
    # src/main/resources/application-prod.yml
    spring:
      datasource:
        url: jdbc:mariadb://localhost:3306/your_db_name
        username: your_db_user
        password: your_db_password
        driver-class-name: org.mariadb.jdbc.Driver
      jpa:
        hibernate:
          ddl-auto: validate # Flyway를 사용하므로 validate로 설정
      security:
        oauth2:
          resourceserver:
            jwt:
              # 64글자 이상의 매우 강력한 무작위 문자열을 사용하세요.
              secret-key: """your_super_strong_and_long_jwt_secret_key_here"""
    ```

### 3. 로컬에서 실행하기

- **개발 프로파일로 실행 (H2 인메모리 DB 사용)**
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=dev'
  ```

- **운영 프로파일로 실행 (MariaDB 사용)**
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=prod'
  ```
  애플리케이션은 기본적으로 `http://localhost:8080` 포트에서 실행됩니다.

## 🐳 Docker로 실행하기

1.  **JAR 파일 빌드**
    ```bash
    ./gradlew clean build -x test
    ```

2.  **Docker 이미지 빌드**
    ```bash
    docker build -t jlpt-backend:latest .
    ```

3.  **Docker 컨테이너 실행**
    ```bash
    docker run -p 8080:8080 \
      -e "SPRING_PROFILES_ACTIVE=prod" \
      -e "SPRING_DATASOURCE_URL=jdbc:mariadb://<host>:<port>/<db>" \
      -e "SPRING_DATASOURCE_USERNAME=<user>" \
      -e "SPRING_DATASOURCE_PASSWORD=<password>" \
      -e "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET-KEY=<your_jwt_secret>" \
      jlpt-backend:latest
    ```
    > **참고**: Docker 실행 시에는 Spring Boot의 표준 환경 변수 형식을 사용하여 설정을 주입하는 것이 좋습니다.

## 📖 API 엔드포인트

자세한 API 명세는 **[API 문서 링크]** 에서 확인하실 수 있습니다.

- `POST /api/auth/signup`: 회원가입
- `POST /api/auth/login`: 로그인 (JWT 발급)
- `GET /api/problems`: 문제 목록 조회
- `POST /api/problems`: AI를 통해 새로운 문제 생성
- `POST /api/user-questions`: 사용자 질문에 대한 AI 답변 요청

## 🗄️ 데이터베이스 마이그레이션

이 프로젝트는 `Flyway`를 사용하여 데이터베이스 스키마의 버전을 관리합니다. 애플리케이션 실행 시 `src/main/resources/db/migration` 경로의 SQL 스크립트가 자동으로 실행되어 스키마를 최신 상태로 유지합니다.

## 🤝 기여하기

기여는 언제나 환영합니다! 이슈를 생성하거나 Pull Request를 보내주세요.

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.
