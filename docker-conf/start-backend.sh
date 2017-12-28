#!/usr/bin/env bash
/usr/src/rate-my-area/rate-my-area-1.0-SNAPSHOT/bin/rate-my-area \
-Dpidfile.path=/dev/null \
-Ds3-access-key=$S3_ACCESS_KEY \
-Ds3-secret-key=$S3_SECRET_KEY \
-Dplay.http.secret.key=$PLAY_SECRET_KEY \
-Dslick.dbs.default.db.url=$DB_URL \
-Dslick.dbs.default.db.user=$DB_USER \
-Dslick.dbs.default.db.password=$DB_PASSWORD
