# Image URLs schema

# --- !Ups

CREATE TABLE image_urls (
    id bigserial primary key,
    url varchar(255) NOT NULL,
    review_id bigint,
    constraint image_urls_review_id_fkey foreign key (review_id) references reviews(id)
);

# --- !Downs

DROP TABLE image_urls;
