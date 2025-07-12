-- src/main/resources/db/migration/V1__initial_schema.sql

CREATE TABLE `answer_records` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `problem_id` BIGINT NOT NULL,
    `user_answer` INT NULL DEFAULT 1,
    `is_correct` BOOLEAN NULL DEFAULT 1,
    `user_records_id` BIGINT NULL,
    `created_at` TIMESTAMP NULL,
    `updated_at` TIMESTAMP NULL
);

CREATE TABLE `problems` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `level` CHAR(2) NULL,
    `problem_type` CHAR(1) NULL,
    `problem_title_parent` VARCHAR(255) NULL,
    `problem_title_child` VARCHAR(255) NULL,
    `problem_content` TEXT NULL,
    `answer_number` INT NULL,
    `explanation` TEXT NULL,
    `created_at` TIMESTAMP NULL,
    `updated_at` TIMESTAMP NULL
);

CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(50) NULL,
    `password` VARCHAR(255) NULL,
    `is_admin` BOOLEAN NULL,
    `created_at` TIMESTAMP NULL,
    `updated_at` TIMESTAMP NULL
);

CREATE TABLE `user_questions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `answer_records` BIGINT NOT NULL,
    `created_at` TIMESTAMP NULL,
    `updated_at` TIMESTAMP NULL
);

CREATE TABLE `user_question_chats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_question_id` BIGINT NOT NULL,
    `content` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    `is_user` BOOLEAN NULL DEFAULT 1,
    `message_order` INT NULL,
    `created_at` TIMESTAMP NULL,
    `updated_at` TIMESTAMP NULL
);

CREATE TABLE `choices` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `problem_id` BIGINT NOT NULL,
    `number` INT NULL,
    `content` VARCHAR(255) NULL,
    `is_correct` BOOLEAN NULL,
    `created_at` TIMESTAMP NULL,
    `updated_at` TIMESTAMP NULL
);


CREATE TABLE `refresh_token` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `email` VARCHAR(50) NULL,
    `refresh_token` VARCHAR(255) NULL,
    `expires_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP NULL
);
