-- users 테이블 데이터
INSERT INTO users (id, email, password) VALUES (1, 'test1@example.com', '1234');
INSERT INTO users (id, email, password) VALUES (2, 'test2@example.com', '1234');

-- problems 테이블 데이터
INSERT INTO problems (id, level, problem_type, problem_title_parent, problem_content, answer_number, explanation, created_at, updated_at)
VALUES (1, 'N3', 'G', '문법 문제1', '이 문장은 [ ] 적절한 것을 고르시오.', 2, '정답 해설 A', NOW(), NOW());
INSERT INTO problems (id, level, problem_type, problem_title_parent, problem_content, answer_number, explanation, created_at, updated_at)
VALUES (2, 'N2', 'V', '어휘 문제1', '밑줄 친 부분과 의미가 가장 가까운 것은?', 3, '정답 해설 B', NOW(), NOW());
INSERT INTO problems (id, level, problem_type, problem_title_parent, problem_content, answer_number, explanation, created_at, updated_at)
VALUES (3, 'N1', 'R', '독해 문제1', '다음 글을 읽고 물음에 답하시오.', 1, '정답 해설 C', NOW(), NOW());


-- answer_records 테이블 데이터
-- user1의 문제1 정답 기록
INSERT INTO answer_records (user_id, user_records_id,problem_id, user_answer, is_correct, created_at, updated_at)
VALUES (1, 1, 1, 2, TRUE, NOW(), NOW());

-- user1의 문제2 오답 기록
INSERT INTO answer_records (user_id, user_records_id, problem_id, user_answer, is_correct, created_at, updated_at)
VALUES (1, 2,  2, 1, FALSE, NOW(), NOW());

-- user1의 문제1 또 다른 오답 기록 (같은 문제라도 다시 틀릴 수 있음)
INSERT INTO answer_records (user_id, user_records_id, problem_id, user_answer, is_correct, created_at, updated_at)
VALUES (1, 3,  1, 3, FALSE, NOW(), NOW());

-- user2의 문제3 정답 기록
INSERT INTO answer_records (user_id, user_records_id, problem_id, user_answer, is_correct, created_at, updated_at)
VALUES (2, 1, 3, 1, TRUE, NOW(), NOW());