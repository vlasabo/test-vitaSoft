drop table if exists roles;
drop table if exists requests;
drop table if exists users;


CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username   VARCHAR(200) UNIQUE NOT NULL,
    "password" VARCHAR(200)        NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status        VARCHAR(20)                  NOT NULL,
    "text"        VARCHAR(500)                 NOT NULL,
    creation_date timestamp                    NOT NULL,
    user_id       BIGINT REFERENCES users (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT REFERENCES users (id) NOT NULL,
    "role"  VARCHAR(20)                  NOT NULL
);