CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username   VARCHAR(200) UNIQUE NOT NULL,
    "password" VARCHAR(200)        NOT NULL,
    "role"     VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS requests
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status        VARCHAR(20),
    "text"        VARCHAR(500),
    creation_date timestamp,
    user_id       BIGINT REFERENCES users (id)
);