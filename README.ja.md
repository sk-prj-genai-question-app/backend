[English](./README.md) | [한국어](./README.ko.md) | [日本語](./README.ja.md)

---

# 📚 JLPT問題生成学習ヘルパー - バックエンド

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/user/repo/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](#-tech-stack)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen.svg)](#-tech-stack)

「生成AIによるJLPT問題生成学習ヘルパー」ウェブアプリケーションのバックエンドサーバーです。ユーザー管理、認証、JLPT問題の生成と記録管理、AIサービス連携など、中核となるビジネスロジックを担当します。

## ✨ 主な機能

- **🔐 ユーザー認証**: JWT(JSON Web Token)ベースの安全なログインおよび会員登録機能。
- **🤖 AI問題生成**: 生成AIモデルと連携し、JLPTの予想問題を動的に生成。
- **📖 問題管理**: 生成された問題を照会、修正、削除するRESTful APIを提供。
- **📈 学習記録管理**: ユーザーの問題解決履歴と正答率を追跡・管理。
- **🗣️ AI質疑応答**: ユーザーの質問をAIモデルに転送し、回答を受け取る機能。
- **👑 管理者機能**: ユーザー、問題、学習記録などを管理できるThymeleafベースの管理者ページ。

## 🛠️ 技術スタック

| 区分 | 技術 | バージョン |
| :--- | :--- | :--- |
| **言語** | Java | 17 |
| **フレームワーク** | Spring Boot | 3.4.7 |
| **ビルドツール** | Gradle | |
| **データベース** | MariaDB (本番), H2 (テスト) | |
| **DBマイグレーション** | Flyway | |
| **認証/セキュリティ**| Spring Security, JJWT | 0.11.5 |
| **ORM** | Spring Data JPA | |
| **テンプレートエンジン**| Thymeleaf | |
| **その他** | Lombok, Actuator, Testcontainers | |

## 🏛️ プロジェクト構造

```
src/main/java/com/rookies3/genaiquestionapp
├── GenAiQuestionAppApplication.java  # Spring Bootメインアプリケーション
├── admin/                  # 管理者機能関連のコントローラー、サービス
├── ai/                     # 外部AIサービス連携ロジック
├── auth/                   # 会員登録、ログインなどユーザー認証
├── config/                 # Security, CORSなどグローバル設定
├── entity/                 # JPAエンティティ (データベーステーブルマッピング)
├── exception/              # グローバル例外処理
├── jwt/                    # JWT生成、検証ユーティリティ
├── problem/                # 問題の生成、照会、管理
├── record/                 # ユーザー学習記録管理
├── userquestion/           # ユーザーの質問処理
└── util/                   # 共通ユーティリティクラス
```

## 🚀 始め方

### 1. 事前要件

- Java 17
- Gradle
- Docker (任意)

### 2. インストールと設定

1.  **リポジトリをクローン**
    ```bash
    git clone https://github.com/your-username/jlpt-backend.git
    cd jlpt-backend
    ```

2.  **アプリケーション設定 (ベストプラクティス)**
    `src/main/resources/`ディレクトリに`application-prod.yml`ファイルを作成し、実際の本番環境に合わせて以下の内容を修正します。ローカル開発時は`application-dev.yml`を作成して使用してください。

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
          ddl-auto: validate # Flywayを使用するためvalidateに設定
      security:
        oauth2:
          resourceserver:
            jwt:
              # 64文字以上の非常に強力なランダム文字列を使用してください。
              secret-key: """your_super_strong_and_long_jwt_secret_key_here"""
    ```

### 3. ローカルでの実行

- **`dev`プロファイルで実行 (H2インメモリDB使用)**
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=dev'
  ```

- **`prod`プロファイルで実行 (MariaDB使用)**
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=prod'
  ```
  アプリケーションはデフォルトで`http://localhost:8080`ポートで実行されます。

## 🐳 Dockerで実行

1.  **JARファイルをビルド**
    ```bash
    ./gradlew clean build -x test
    ```

2.  **Dockerイメージをビルド**
    ```bash
    docker build -t jlpt-backend:latest .
    ```

3.  **Dockerコンテナを実行**
    ```bash
    docker run -p 8080:8080 \
      -e "SPRING_PROFILES_ACTIVE=prod" \
      -e "SPRING_DATASOURCE_URL=jdbc:mariadb://<host>:<port>/<db>" \
      -e "SPRING_DATASOURCE_USERNAME=<user>" \
      -e "SPRING_DATASOURCE_PASSWORD=<password>" \
      -e "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET-KEY=<your_jwt_secret>" \
      jlpt-backend:latest
    ```
    > **注意**: Dockerで実行する際は、Spring Bootの標準的な環境変数形式を使用して設定を注入することをお勧めします。

## 📖 APIエンドポイント

詳細なAPI仕様は**[APIドキュメントへのリンク]**で確認できます。

- `POST /api/auth/signup`: 新規ユーザー登録
- `POST /api/auth/login`: ログイン (JWT発行)
- `GET /api/problems`: 問題リスト照会
- `POST /api/problems`: AIを介して新しい問題を生成
- `POST /api/user-questions`: ユーザーの質問に対するAIの回答を要求

## 🗄️ データベースマイグレーション

このプロジェクトは`Flyway`を使用してデータベーススキーマのバージョンを管理します。アプリケーション起動時に`src/main/resources/db/migration`パスのSQLスクリプトが自動的に実行され、スキーマを最新の状態に保ちます。

## 🤝 貢献

貢献はいつでも歓迎します！Issueを作成するか、Pull Requestを送ってください。

## 📄 ライセンス

このプロジェクトはMITライセンスに従います。詳細については`LICENSE`ファイルを参照してください。
