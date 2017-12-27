FROM ubuntu:16.04

# Install pre-requisites

RUN apt-get update
RUN apt-get install -y supervisor unzip software-properties-common python-software-properties apt-transport-https

# Install Java

RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections
RUN apt-get install -y oracle-java8-installer
RUN apt-get install -y oracle-java8-set-default

# Install nginx and LetsEncrypt Certbot

RUN add-apt-repository ppa:certbot/certbot
RUN apt-get update
RUN apt-get install -y nginx python-certbot-nginx

# Add build and config

ADD docker-conf /usr/src/rate-my-area/docker-conf
ADD target/universal/rate-my-area-1.0-SNAPSHOT.zip /usr/src/rate-my-area

# Extract build

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
