#!/usr/bin/env bash
certbot certonly --standalone --non-interactive --agree-tos --email hello@stuartp.io \
--domains ratemyarea.stuartp.io --cert-name ratemyarea && nginx
