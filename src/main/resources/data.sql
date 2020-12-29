INSERT INTO PUBLIC.USERS (ID, NAME, PASSWORD, IS_ADMIN)
VALUES (1, 'Admin', '{noop}pass', true),
       (2, 'User', '{noop}pass', false);

INSERT INTO PUBLIC.RESTAURANTS (ID, NAME)
VALUES (1, 'First'),
       (2, 'Second');

INSERT INTO PUBLIC.MENUS (ID, RESTAURANT_ID, DATE)
VALUES (1, 1, '2020-12-25'),
       (2, 1, '2020-12-24'),
       (3, 2, '2020-12-25');

INSERT INTO PUBLIC.DISHES (ID, MENU_ID, NAME, PRICE)
VALUES (1, 1, 'Fish', 10),
       (2, 1, 'Potato', 2),
       (3, 2, 'Tomatoes', 3),
       (4, 3, 'Beacon', 15);

INSERT INTO PUBLIC.VOTES (ID, USER_ID, RESTAURANT_ID, DATE)
VALUES (1, 1, 1,  '2020-12-25'),
       (2, 2, 1,  '2020-12-25'),
       (3, 1, 2,  '2020-12-25');