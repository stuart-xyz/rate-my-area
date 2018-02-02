# Rate My Area

A website for submitting photos and reviews of your area, built to demonstrate the use of a modern Scala tech stack: Play Framework 2.6, Macwire compile-time DI, Slick ORM, ScalaTest, React and Webpack.

The application is packaged into a Docker image - just add your own API keys to run in production. TLS certificates are automatically generated and renewed using LetsEncrypt Certbot.

UI files live in a separate repository at https://github.com/stuart-xyz/rate-my-area-ui.

An example deployment is hosted on AWS, at https://ratemyarea.stuartp.io, as shown here:
![diagram](diagram.png)

## Dependencies

* JDK installation
* SBT installation
* Docker installation
* PostgreSQL server

## Run

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

## Test

```
sbt test
```

Tests use an in-memory H2 database for storage, and Mockito to mock dependencies.
