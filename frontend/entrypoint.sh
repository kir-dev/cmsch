#!/bin/sh
set -e

echo "Loading cmsch frontend"
echo ${VITE_API_BASE_URL}

NGINX_HTML=/usr/share/nginx/html

# Substitute HTML-level env vars into index.html (icons, fonts, manifest, error reporter, Plausible)
envsubst '${VITE_API_BASE_URL} ${VITE_NAME} ${VITE_DESCRIPTION} ${VITE_THEME_COLOR} ${VITE_PLAUSIBLE_URL}' \
  < "$NGINX_HTML/index.html" > /tmp/index.html
mv /tmp/index.html "$NGINX_HTML/index.html"

# Generate JS runtime config — read by the app instead of the baked import.meta.env values
cat > "$NGINX_HTML/env-config.js" <<EOF
window.__env__ = {
  VITE_API_BASE_URL: "${VITE_API_BASE_URL:-http://localhost:8080}",
  VITE_CLIENT_BASE_URL: "${VITE_CLIENT_BASE_URL:-http://localhost:3000}",
  VITE_NAME: "${VITE_NAME:-CMSch Web}",
  VITE_DESCRIPTION: "${VITE_DESCRIPTION:-CMSch Web}",
  VITE_THEME_COLOR: "${VITE_THEME_COLOR:-#ffffff}",
  VITE_DISABLE_APP_CONFIG_CACHE: "${VITE_DISABLE_APP_CONFIG_CACHE:-false}",
  VITE_APP_CONFIG_CACHE_TTL_SECONDS: "${VITE_APP_CONFIG_CACHE_TTL_SECONDS:-600}",
  VITE_PASS_SERVER_URL: "${VITE_PASS_SERVER_URL:-https://pass.kir-dev.hu}",
  VITE_PASS_TEMPLATE: "${VITE_PASS_TEMPLATE:-generic}",
  VITE_OFFICIAL_LANGUAGE: "${VITE_OFFICIAL_LANGUAGE:-false}",
  VITE_NEW_RIDDLE_ENDPOINTS: "${VITE_NEW_RIDDLE_ENDPOINTS:-true}",
  VITE_HIDE_KIR_DEV_IN_FOOTER: "${VITE_HIDE_KIR_DEV_IN_FOOTER:-false}",
  VITE_PLAUSIBLE_URL: "${VITE_PLAUSIBLE_URL:-}",
  VITE_FIREBASE_PROJECT_ID: "${VITE_FIREBASE_PROJECT_ID:-}",
  VITE_FIREBASE_API_KEY: "${VITE_FIREBASE_API_KEY:-}",
  VITE_FIREBASE_SENDER_ID: "${VITE_FIREBASE_SENDER_ID:-}",
  VITE_FIREBASE_APP_ID: "${VITE_FIREBASE_APP_ID:-}",
  VITE_FIREBASE_WEB_PUSH_PUBLIC_KEY: "${VITE_FIREBASE_WEB_PUSH_PUBLIC_KEY:-}"
};
EOF

exec nginx -g "daemon off;"
