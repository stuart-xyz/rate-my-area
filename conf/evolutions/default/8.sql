# Users schema

# --- !Ups

ALTER TABLE users ADD CONSTRAINT username_uniqueness UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT email_uniqueness UNIQUE (email);

# --- !Downs

ALTER TABLE users DROP CONSTRAINT email_uniqueness;
ALTER TABLE users DROP CONSTRAINT username_uniqueness;
