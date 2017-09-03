# Image URLs schema

# --- !Ups

CREATE TABLE image_urls (
    id bigserial primary key,
    url varchar(255) NOT NULL,
    review_id bigint references reviews(id)
);

# --- !Downs

DROP TABLE image_urls;
