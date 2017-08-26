# Users schema

# --- !Ups

ALTER TABLE users ADD salt varchar(255) NOT NULL;

# --- !Downs

ALTER TABLE users DROP salt;
