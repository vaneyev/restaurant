create sequence GLOBAL_SEQUENCE
    minvalue 100;

create table RESTAURANTS
(
    ID   BIGINT default NEXT VALUE FOR "PUBLIC"."GLOBAL_SEQUENCE" not null
        primary key,
    NAME VARCHAR
);