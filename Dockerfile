FROM stuartxyz/scala-web-app

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

RUN cp -r /usr/src/rate-my-area/conf/ /usr/src/rate-my-area/rate-my-area-1.0-SNAPSHOT/
RUN cp docker-conf/nginx/nginx.conf /etc/nginx/nginx.conf
RUN cp docker-conf/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Run command

CMD ["/usr/bin/supervisord"]
