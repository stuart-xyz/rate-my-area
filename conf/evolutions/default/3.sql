# Users schema

# --- !Ups

ALTER TABLE users RENAME COLUMN fullname TO full_name;
ALTER TABLE users RENAME COLUMN isadmin TO is_admin;

# --- !Downs

ALTER TABLE users RENAME COLUMN full_name TO fullname;
ALTER TABLE users RENAME COLUMN is_admin TO isadmin;
