# Users schema

# --- !Ups

CREATE TABLE users (
    id bigserial primary key,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullName varchar(255) NOT NULL,
    isAdmin boolean NOT NULL
);

# --- !Downs

DROP TABLE users;
