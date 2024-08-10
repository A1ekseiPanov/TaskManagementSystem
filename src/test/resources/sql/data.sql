INSERT INTO dbo.users(email, first_name, last_name, password)
VALUES ('email@ya.ru', 'Петр', 'Петровкин', 'пароль'),
       ('eemail@eya.ru', 'Василий', 'Перкин', 'пароль');
INSERT INTO dbo.user_role(user_id, role)
VALUES (3, 'USER'),
       (4, 'USER');

INSERT INTO dbo.tasks(status_id, user_id, header, description, priority)
VALUES (1, 1, 'Заголовок1', 'Описание1', 3),
       (1, 1, 'Заголовок2', 'Описание2', 3),
       (1, 1, 'Заголовок3', 'Описание3', 3);

INSERT INTO dbo.tasks_performers(performer_id, task_id)
VALUES (2, 1),
       (2, 2),
       (3, 1),
       (3, 2),
       (3, 3);
INSERT INTO dbo.coments (author_id, task_id, comment)
VALUES (1, 1, 'Комент1'),
       (2, 1, 'Комент2'),
       (3, 1, 'Комент3');
