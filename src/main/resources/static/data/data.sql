INSERT INTO USERS(created_date, modified_date, username, password, email, role, status) values
('2023-05-17 11:48:00', '2023-05-17 11:48:00', 'kkr', '1234', 'kkr@nate.com', 'USER', 'ACTIVE'),
('2023-05-17 11:48:00', '2023-05-17 11:48:00', 'khh', '1234', 'khh@nate.com', 'USER', 'ACTIVE'),
('2023-05-17 11:48:00', '2023-05-17 11:48:00', 'shs', '1234', 'shs@nate.com', 'USER', 'ACTIVE'),
('2023-05-17 11:48:00', '2023-05-17 11:48:00', 'jhs', '1234', 'jhs@nate.com', 'USER', 'ACTIVE'),
('2023-05-17 11:48:00', '2023-05-17 11:48:00', 'pjh', '1234', 'pjh@nate.com', 'USER', 'ACTIVE');

INSERT INTO BOARDS(created_date, modified_date, title, content, user_id, status) values
('2023-05-17 12:14:00', '2023-05-17 12:14:00', '첫 번째 게시물 입니다.', '첫 번째 게시물 내용.', 1, 'ACTIVE'),
('2023-05-17 12:14:00', '2023-05-17 12:14:00', '두 번째 게시물 입니다.', '두 번째 게시물 내용.', 2, 'ACTIVE'),
('2023-05-17 12:14:00', '2023-05-17 12:14:00', '세 번째 게시물 입니다.', '세 번째 게시물 내용.', 3, 'ACTIVE'),
('2023-05-17 12:14:00', '2023-05-17 12:14:00', '네 번째 게시물 입니다.', '네 번째 게시물 내용.', 4, 'ACTIVE'),
('2023-05-17 12:14:00', '2023-05-17 12:14:00', '다섯 번째 게시물 입니다.', '다섯 번째 게시물 내용.', 5, 'ACTIVE');

INSERT INTO ADMIN_LIST(created_date, modified_date, username, password, role) values
('2023-05-17 11:48:00', '2023-05-17 11:48:00', 'admin', '1234', 'ADMIN');

commit;