INSERT INTO PUBLIC.USERS (ID, NAME, PASSWORD, IS_ADMIN)
VALUES (1, 'Admin', '{noop}pass', true),
       (2, 'User', '{noop}pass', false);
INSERT INTO PUBLIC.RESTAURANTS (ID, NAME)
VALUES (1, 'First'),
       (2, 'Second');