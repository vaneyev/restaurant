create sequence GLOBAL_SEQUENCE
    minvalue 100;

create table RESTAURANTS
(
    ID   BIGINT
        default NEXT VALUE FOR "PUBLIC"."GLOBAL_SEQUENCE"
        not null
        primary key,
    NAME VARCHAR
        not null
);

create table USERS
(
    ID       BIGINT
        default NEXT VALUE FOR "PUBLIC"."GLOBAL_SEQUENCE"
        not null
        primary key,
    NAME     VARCHAR
        not null,
    PASSWORD VARCHAR
        not null,
    IS_ADMIN BOOLEAN
        not null
);

create unique index USERS_NAME_UINDEX
    on USERS (NAME);

create table MENUS
(
    ID            BIGINT
        default NEXT VALUE FOR "PUBLIC"."GLOBAL_SEQUENCE"
        not null,
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
    ID      BIGINT
        default NEXT VALUE FOR "PUBLIC"."GLOBAL_SEQUENCE"
        not null,
    NAME    VARCHAR
        not null,
    MENU_ID BIGINT
        not null,
    PRICE   INT
        not null,
    constraint MENU_ITEMS_PK
        primary key (ID),
    constraint MENU_ITEMS_MENUS_ID_FK
        foreign key (MENU_ID) references MENUS (ID)
            on delete cascade
);

create table VOTES
(
    ID            BIGINT
        default NEXT VALUE FOR "PUBLIC"."GLOBAL_SEQUENCE"
        not null,
    USER_ID       BIGINT
        not null,
    RESTAURANT_ID BIGINT
        not null,
    DATE          DATE
        not null,
    constraint VOTES_PK
        primary key (ID),
    constraint VOTES_RESTAURANTS_ID_FK
        foreign key (RESTAURANT_ID) references RESTAURANTS (ID),
    constraint VOTES_USERS_ID_FK
        foreign key (USER_ID) references USERS (ID)
);

create unique index VOTES_USER_ID_RESTAURANT_ID_DATE_UINDEX
    on VOTES (USER_ID, RESTAURANT_ID, DATE);

