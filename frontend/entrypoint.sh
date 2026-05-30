#!/bin/sh
set -e

HTML=/usr/share/nginx/html/index.html

for var in $(env | grep '^VITE_' | cut -d= -f1); do
  placeholder="__${var}__"
  value=$(printenv "$var" | sed 's/[&/\]/\\&/g')
  sed -i "s|${placeholder}|${value}|g" "$HTML"
done

exec nginx -g "daemon off;"
