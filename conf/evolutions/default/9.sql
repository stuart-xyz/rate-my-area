# Image URLs schema

# --- !Ups

ALTER TABLE image_urls
DROP CONSTRAINT image_urls_review_id_fkey,
ADD CONSTRAINT image_urls_review_id_fkey
   FOREIGN KEY (review_id)
   REFERENCES reviews(id)
   ON DELETE CASCADE;

# --- !Downs

ALTER TABLE image_urls
DROP CONSTRAINT image_urls_review_id_fkey,
ADD CONSTRAINT image_urls_review_id_fkey
   FOREIGN KEY (review_id)
   REFERENCES reviews(id);
