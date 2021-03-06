INSERT INTO public.roles (id, name)
VALUES (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER');

INSERT INTO public.users (id, name, password, is_admin)
VALUES (1, 'Admin', '{noop}pass', TRUE),
       (2, 'User', '{noop}pass', FALSE);

INSERT INTO public.user_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2);

INSERT INTO public.restaurants (id, name)
VALUES (1, 'First'),
       (2, 'Second');

INSERT INTO public.menus (id, restaurant_id, menu_date)
VALUES (1, 1, '2020-12-25'),
       (2, 1, '2020-12-24'),
       (3, 2, '2020-12-25');

INSERT INTO public.dishes (id, name)
VALUES (1, 'Fish'),
       (2, 'Potato'),
       (3, 'Tomatoes'),
       (4, 'Beacon');

INSERT INTO public.menu_items (id, menu_id, dish_id, price)
VALUES (1, 1, 1, 10),
       (2, 1, 2, 2),
       (3, 2, 3, 3),
       (4, 3, 4, 15);

INSERT INTO public.votes (id, user_id, restaurant_id, date)
VALUES (1, 1, 1, '2020-12-25'),
       (2, 2, 2, '2020-12-25');
