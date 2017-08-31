# Reviews schema

# --- !Ups

ALTER TABLE reviews DROP COLUMN emoji_code;

# --- !Downs

ALTER TABLE reviews ADD COLUMN emoji_code varchar(255) NOT NULL DEFAULT '';
