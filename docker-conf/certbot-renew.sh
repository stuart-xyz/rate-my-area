#!/usr/bin/env bash
echo "------------"
echo "$(date): Attempting Certificate Renewal" >> /var/log/letsencrypt/renewal.log
certbot renew >> /var/log/letsencrypt/renewal.log 2>&1
echo "------------"
