# Reviews schema

# --- !Ups

CREATE TABLE reviews (
    id bigserial primary key,
    title varchar(255) NOT NULL,
    area_name varchar(255) NOT NULL,
    emoji_code varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    user_id bigint references users(id)
);

# --- !Downs

DROP TABLE reviews;
