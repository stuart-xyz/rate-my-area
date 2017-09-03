# Reviews schema

# --- !Ups

ALTER TABLE reviews ADD COLUMN image_urls varchar(255)[] NOT NULL DEFAULT array[]::varchar(255)[];

# --- !Downs

ALTER TABLE reviews DROP COLUMN image_urls;
