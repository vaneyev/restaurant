drop table if exists DISHES;
drop table if exists VOTES;
drop table if exists MENUS;
drop table if exists RESTAURANTS;
drop table if exists USERS;

create table RESTAURANTS
(
    ID   IDENTITY primary key,
    NAME VARCHAR not null
);

create table USERS
(
    ID       IDENTITY primary key,
    NAME     VARCHAR not null,
    PASSWORD VARCHAR not null,
    IS_ADMIN BOOLEAN not null
);

create unique index USERS_NAME_UINDEX
    on USERS (NAME);

create table MENUS
(
    ID            IDENTITY primary key,
    RESTAURANT_ID BIGINT
        not null,
    DATE          DATE
        not null,
    constraint MENUS_PK
        primary key (ID),
    constraint MENUS_RESTAURANTS_ID_FK
        foreign key (RESTAURANT_ID) references RESTAURANTS (ID)
            on delete cascade
);

create unique index MENUS_RESTAURANT_ID_DATE_UINDEX
    on MENUS (RESTAURANT_ID, DATE);

create table DISHES
(
    ID      IDENTITY primary key,
    NAME    VARCHAR not null,
    MENU_ID BIGINT  not null,
    PRICE   INT     not null,
    constraint MENU_ITEMS_PK
        primary key (ID),
    constraint MENU_ITEMS_MENUS_ID_FK
        foreign key (MENU_ID) references MENUS (ID)
            on delete cascade
);

create table VOTES
(
    ID            IDENTITY primary key,
    USER_ID       BIGINT not null,
    RESTAURANT_ID BIGINT not null,
    DATE          DATE   not null,
    constraint VOTES_RESTAURANTS_ID_FK
        foreign key (RESTAURANT_ID) references RESTAURANTS (ID)
            on delete cascade,
    constraint VOTES_USERS_ID_FK
        foreign key (USER_ID) references USERS (ID)
);

create unique index VOTES_USER_ID_DATE_UINDEX
    on VOTES (USER_ID, DATE);

