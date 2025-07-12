# 📚 JLPT 문제 생성 학습 도우미 - 백엔드

## 1. 🚀 프로젝트 소개

이 프로젝트는 "생성형 AI를 통한 JLPT 문제 생성 학습 도우미" 웹 애플리케이션의 백엔드 부분입니다. 사용자 인증, JLPT 문제 관리, 사용자 답변 기록, 그리고 AI 모델과의 연동을 통해 문제 생성 및 사용자 질문 처리 기능을 제공합니다.

## 2. 🛠️ 기술 스택

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.4.7
- **빌드 도구**: Gradle
- **데이터베이스**:
  - MariaDB (운영 환경)
  - H2 Database (테스트 환경)
- **인증/보안**: Spring Security, JJWT (JSON Web Token), BCrypt
- **템플릿 엔진**: Thymeleaf (관리자 페이지 등)
- **기타**: Spring Data JPA, Lombok, Spring Boot DevTools

## 3. ✨ 주요 기능

- **사용자 관리**: 회원가입, 로그인, 사용자 정보 관리
- **인증 및 권한 부여**: JWT 기반 인증, 사용자 및 관리자 권한 관리
- **JLPT 문제 관리**:
  - AI를 통한 JLPT 문제 생성 요청 및 저장
  - 문제 조회, 수정, 삭제
- **사용자 답변 기록**: 사용자의 문제 풀이 기록 저장 및 조회
- **AI 연동**:
  - 외부 AI 서비스와 연동하여 JLPT 문제 생성
  - 사용자 질문에 대한 AI 답변 처리
- **관리자 기능**: 사용자, 문제, 답변 기록 등에 대한 관리 기능 (Thymeleaf 기반 웹 페이지)

## 4. ⚙️ 환경 설정

프로젝트를 로컬에서 실행하기 위해 다음 환경 변수 설정이 필요합니다. 프로젝트 루트에 `.env` 파일을 생성하고 아래 내용을 채워주세요.

```
# JWT Secret Key (보안을 위해 강력하고 긴 문자열 사용 권장)
JWT_SECRET=your_jwt_secret_key_here

# MariaDB 설정 (운영 환경)
DB_HOST=localhost
DB_PORT=3306
DB_NAME=your_database_name
DB_USER=your_database_user
DB_PASSWORD=your_database_password
```

**참고**: 테스트 환경에서는 H2 Database를 사용하므로 별도의 DB 설정이 필요 없습니다.

## 5. ▶️ 실행 방법

1.  **환경 변수 설정**: 위 4번 항목을 참조하여 `.env` 파일을 설정합니다.
2.  **Gradle 빌드**: 프로젝트 루트에서 다음 명령어를 실행하여 의존성을 다운로드하고 프로젝트를 빌드합니다.
    ```bash
    ./gradlew clean build
    ```
3.  **애플리케이션 실행**: 빌드가 완료되면 다음 명령어로 애플리케이션을 실행합니다.
    ```bash
    java -jar build/libs/GenAiQuestionApp-0.0.1-SNAPSHOT.jar
    ```
    또는 Spring Boot DevTools를 사용하여 개발 모드로 실행할 수 있습니다.
    ```bash
    ./gradlew bootRun
    ```
4.  **접속**: 애플리케이션은 기본적으로 `http://localhost:8080` 포트에서 실행됩니다.

## 6. 📖 API 문서

API 문서는 docs 리포지토리에서 확인할 수 있습니다.

## 7. 📄 라이선스

이 프로젝트는 MIT License를 따릅니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.
