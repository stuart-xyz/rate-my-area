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

* JDK installation
* SBT installation
* Docker installation
* PostgreSQL server

## Test

```
sbt test
```

Tests use an in-memory H2 database for storage, and Mockito to mock dependencies.

## Run

First build the UI, then copy build output to `public/compiled`.
```
git submodule init
git submodule update
cd ui
npm run build
cp -r build ../public/compiled
```

Run the Play application with SBT from the root directory.
```
sbt "run -Dconfig.resource=/path/to/secrets.conf"
```

What you need in `secrets.conf`:
```
s3-access-key = "XXX"
s3-secret-key = "XXX"
play.http.secret.key = "XXX"
```

Also change `s3-region` and `s3-bucket-name` in `application.conf` for your own setup.

## Automated deployment with CircleCI

A CircleCI config is provided - simply replace with your own details and make sure your AWS credentials are stored in CircleCI project settings. `$AWS_REPOSITORY_URL` also needs to be provided as an environment variable inside CircleCI.

## Manual deployment

Build the Docker image:
```
docker build -t stuartp.io/ratemyarea --build-arg BUILD_ENV=[local/dev/prod] .
```

Build environments:
* `local`: just the backend runs, no attempt to generate a TLS certificate.
* `dev`: also attempts to generate a test TLS certificate from LetsEncrypt.
* `prod`: attempts to generate a real TLS certificate from LetsEncrypt.
