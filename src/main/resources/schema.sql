DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS users;

CREATE TABLE restaurants
(
    id   IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE users
(
    id       IDENTITY PRIMARY KEY,
    name     VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    is_admin BOOLEAN NOT NULL
);

CREATE UNIQUE INDEX users_name_uindex
    ON users (name);

CREATE TABLE menus
(
    id            IDENTITY PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    date          DATE   NOT NULL,
    CONSTRAINT menus_pk
        PRIMARY KEY (id),
    CONSTRAINT menus_restaurants_id_fk
        FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
            ON DELETE CASCADE
);

CREATE UNIQUE INDEX menus_restaurant_id_date_uindex
    ON menus (restaurant_id, date);

CREATE TABLE dishes
(
    id      IDENTITY PRIMARY KEY,
    name    VARCHAR NOT NULL,
    menu_id BIGINT  NOT NULL,
    price   INT     NOT NULL,
    CONSTRAINT menu_items_pk
        PRIMARY KEY (id),
    CONSTRAINT menu_items_menus_id_fk
        FOREIGN KEY (menu_id) REFERENCES menus (id)
            ON DELETE CASCADE
);

CREATE TABLE votes
(
    id            IDENTITY PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    date          DATE   NOT NULL,
    CONSTRAINT votes_restaurants_id_fk
        FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
            ON DELETE CASCADE,
    CONSTRAINT votes_users_id_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE UNIQUE INDEX votes_user_id_date_uindex
    ON votes (user_id, date);

