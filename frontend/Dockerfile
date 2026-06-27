# Use a Node.js base image for building the application
FROM docker.io/node:24-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package.json package-lock.json ./

# Install dependencies
RUN npm ci

# Copy the rest of the application code to the working directory
COPY . .

# Only HTML-level variables are needed at build time (Vite substitutes %VAR% in index.html).
# They are baked as __PLACEHOLDER__ strings so the image stays environment-agnostic;
# actual values are injected by entrypoint.sh at container start via envsubst.
# All JS-only variables are provided at runtime through env-config.js — no ARGs needed.
ARG VITE_API_BASE_URL=__VITE_API_BASE_URL__
ARG VITE_NAME=__VITE_NAME__
ARG VITE_DESCRIPTION=__VITE_DESCRIPTION__
ARG VITE_THEME_COLOR=__VITE_THEME_COLOR__
ARG VITE_PLAUSIBLE_URL=__VITE_PLAUSIBLE_URL__

ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_NAME=$VITE_NAME
ENV VITE_DESCRIPTION=$VITE_DESCRIPTION
ENV VITE_THEME_COLOR=$VITE_THEME_COLOR
ENV VITE_PLAUSIBLE_URL=$VITE_PLAUSIBLE_URL

# Build the application
RUN npm run build

# Convert __VITE_*__ placeholders to ${VITE_*} so envsubst can replace them at container start
RUN sed -i \
  -e 's|__VITE_API_BASE_URL__|${VITE_API_BASE_URL}|g' \
  -e 's|__VITE_NAME__|${VITE_NAME}|g' \
  -e 's|__VITE_DESCRIPTION__|${VITE_DESCRIPTION}|g' \
  -e 's|__VITE_THEME_COLOR__|${VITE_THEME_COLOR}|g' \
  -e 's|__VITE_PLAUSIBLE_URL__|${VITE_PLAUSIBLE_URL}|g' \
  /app/dist/index.html

# Use a separate Nginx base image for serving the built application
FROM docker.io/nginx:alpine

# envsubst (from gettext) is used by entrypoint.sh to inject HTML-level env vars
RUN apk add --no-cache gettext

# Copy the built application files to the Nginx web server directory
COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Expose the desired port (default is 80 for Nginx)
EXPOSE 80

ENTRYPOINT ["/entrypoint.sh"]
