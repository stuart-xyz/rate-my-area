# Reviews schema

# --- !Ups

ALTER TABLE reviews DROP CONSTRAINT reviews_user_id_fkey;
ALTER TABLE reviews ADD CONSTRAINT reviews_user_id_fkey
   FOREIGN KEY (user_id)
   REFERENCES users(id)
   ON DELETE CASCADE;

# --- !Downs

ALTER TABLE reviews DROP CONSTRAINT reviews_user_id_fkey;
ALTER TABLE reviews ADD CONSTRAINT reviews_user_id_fkey
   FOREIGN KEY (user_id)
   REFERENCES users(id);
