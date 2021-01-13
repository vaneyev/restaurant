INSERT INTO public.users (id, name, password, is_admin)
VALUES (1, 'Admin', '{noop}pass', TRUE),
       (2, 'User', '{noop}pass', FALSE);

INSERT INTO public.restaurants (id, name)
VALUES (1, 'First'),
       (2, 'Second');

INSERT INTO public.menus (id, restaurant_id, date)
VALUES (1, 1, '2020-12-25'),
       (2, 1, '2020-12-24'),
       (3, 2, '2020-12-25');

INSERT INTO public.dishes (id, menu_id, name, price)
VALUES (1, 1, 'Fish', 10),
       (2, 1, 'Potato', 2),
       (3, 2, 'Tomatoes', 3),
       (4, 3, 'Beacon', 15);

INSERT INTO public.votes (id, user_id, restaurant_id, date)
VALUES (1, 1, 1,  '2020-12-25'),
       (2, 2, 2,  '2020-12-25');
