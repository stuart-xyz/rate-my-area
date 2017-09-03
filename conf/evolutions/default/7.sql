# Reviews / image URLs schema

# --- !Ups

ALTER TABLE reviews DROP COLUMN image_urls;
CREATE TABLE image_urls (
    id bigserial primary key,
    url varchar(255) NOT NULL,
    review_id bigint references reviews(id)
);

# --- !Downs

DROP TABLE image_urls;
ALTER TABLE reviews ADD COLUMN image_urls varchar(255)[] NOT NULL DEFAULT array[]::varchar(255)[];
