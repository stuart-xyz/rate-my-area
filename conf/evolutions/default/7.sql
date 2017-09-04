# Users schema

# --- !Ups

ALTER TABLE users DROP COLUMN is_admin;
ALTER TABLE users RENAME COLUMN full_name TO username;

# --- !Downs

ALTER TABLE users RENAME COLUMN username TO full_name;
ALTER TABLE users ADD COLUMN is_admin boolean NOT NULL;
