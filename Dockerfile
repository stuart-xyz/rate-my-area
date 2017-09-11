FROM stuartxyz/scala-web-app

# Install extra pre-requisites

RUN apt-get install -y supervisor unzip

# Add source

ADD . /usr/src/rate-my-area
RUN mkdir -p /usr/src/rate-my-area/.sbt
RUN mkdir -p /usr/src/rate-my-area/.ivy2
RUN cp -r /usr/src/rate-my-area/.sbt /root
RUN cp -r /usr/src/rate-my-area/.ivy2 /root

# Build

WORKDIR /usr/src/rate-my-area
RUN sbt dist
RUN unzip /usr/src/rate-my-area/target/universal/rate-my-area-1.0-SNAPSHOT.zip

# Copy config

ARG BUILD_ENV
RUN touch docker-conf/dev/nginx.conf
RUN cp docker-conf/$BUILD_ENV/nginx.conf /etc/nginx/nginx.conf
RUN cp docker-conf/$BUILD_ENV/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
RUN mkdir -p /var/log/supervisor

# Run command

CMD ["/usr/bin/supervisord"]
