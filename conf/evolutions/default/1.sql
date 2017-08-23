# Users schema

# --- !Ups

CREATE TABLE users (
    id bigserial primary key,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isadmin boolean NOT NULL
);

# --- !Downs

DROP TABLE users;
