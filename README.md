# Rate My Area

A website for submitting photos and reviews of your area, built to demonstrate the use of a modern Scala tech stack: Play Framework 2.6, Macwire compile-time DI, Slick ORM, ScalaTest, React and Webpack.

Included:
* Continuous integration and delivery scripts (Docker and CircleCI).
* Example nginx and supervisor configurations.
* Database evolution scripts.
* Lots more example code for whole stack described above.
* A few pictures of London.

Database evolutions will automatically set up a fresh Postgres database. The application is packaged into a Docker image - just add your own API keys to run in production. TLS certificates can be automatically generated and renewed using LetsEncrypt Certbot.

UI files live in a separate repository at https://github.com/stuart-xyz/rate-my-area-ui.

An example deployment is hosted on AWS, at https://ratemyarea.stuartp.io, as shown here:
![diagram](diagram.png)

## Dependencies

* JDK 8 installation
* SBT 1.X installation
* Node.js 10 installation
* Docker installation
* Postgres server (developed with 9.6)

## Test

```bash
sbt test
```

Tests use an in-memory H2 database for storage, and Mockito to mock dependencies.

## Run

First build the UI, then copy build output to `public/compiled`.
```bash
git submodule init
git submodule update
cd ui
npm run build
cp -r build ../public/compiled
```

Create a database named `ratemyarea` (or change the `application.conf` database URL). Run the Play application with SBT from the root directory. `secrets.conf` must be placed in `conf/`.
```
sbt "run -Dconfig.resource=secrets.conf"
```

What you need in `secrets.conf`:
```
include "application.conf"

s3-access-key = "XXX"
s3-secret-key = "XXX"
play.http.secret.key = "XXX"
```

Also change `s3-region` and `s3-bucket-name` in `application.conf` for your own setup.

## Automated deployment with CircleCI

A CircleCI config is provided - simply replace with your own details and make sure your AWS credentials are stored in CircleCI project settings. `$AWS_REPOSITORY_URL` also needs to be provided as an environment variable inside CircleCI.

## Manual deployment

Build the Docker image:
```bash
sbt dist
docker build -t stuartp.io/ratemyarea --build-arg BUILD_ENV=[local/dev/prod] .
```

The credentials in `docker-conf/start-backend.sh` must be provided as environment variables to run the image locally, e.g.
```bash
docker run --rm -i -p 80:80 -p 5432:5432 -e "S3_ACCESS_KEY=XXX" -e "S3_SECRET_KEY=XXX" -e "PLAY_SECRET_KEY=XXX" -e "DB_URL=jdbc:postgresql://docker.for.mac.host.internal:5432/ratemyarea" -e "DB_USER=XXX" -e "DB_PASSWORD=XXX" stuartp.io/ratemyarea
```

(If running the Docker image locally, you can edit your hosts file to point `ratemyarea.stuartp.io` to `localhost`, but remember to change the entry back afterwards! Note that only HTTP will be available in local mode.)

Build environments:
* `local`: just the backend runs, no attempt to generate a TLS certificate.
* `dev`: also attempts to generate a test TLS certificate from LetsEncrypt.
* `prod`: attempts to generate a real TLS certificate from LetsEncrypt.
