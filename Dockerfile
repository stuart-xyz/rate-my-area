FROM stuartxyz/scala-web-app

# Install extra pre-requisites

RUN apt-get install -y supervisor unzip

# Add source

ADD docker-conf /usr/src/rate-my-area/docker-conf
ADD target/universal/rate-my-area-1.0-SNAPSHOT.zip /usr/src/rate-my-area

# Build

WORKDIR /usr/src/rate-my-area
RUN unzip /usr/src/rate-my-area/rate-my-area-1.0-SNAPSHOT.zip

# Set up automated SSL certificate renewal

RUN mkdir -p /var/log/letsencrypt
RUN chmod a+x docker-conf/certbot-init.sh
RUN chmod a+x docker-conf/certbot-renew.sh
RUN cp docker-conf/schedule-certbot-renew /etc/cron.d/schedule-certbot-renew

# Copy config

ARG BUILD_ENV
RUN touch docker-conf/dev/nginx.conf
RUN cp docker-conf/$BUILD_ENV/nginx.conf /etc/nginx/nginx.conf
RUN cp docker-conf/$BUILD_ENV/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
RUN mkdir -p /var/log/supervisor

# Run command

CMD ["/usr/bin/supervisord"]
