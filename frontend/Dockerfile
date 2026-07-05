# Use a Node.js base image for building the application
FROM docker.io/node:24-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package.lock (or package-lock.json) to the working directory
COPY package.json package-lock.json ./

# Install dependencies
RUN npm ci

# Copy the rest of the application code to the working directory
COPY . .
COPY .env.docker .env

# Build the application
RUN npm run build

# Use a separate Nginx base image for serving the built application
FROM docker.io/nginx:alpine

# Copy the built application files to the Nginx web server directory
COPY --from=build /app/dist /usr/share/nginx/html

RUN sed -i '/^user /d' /etc/nginx/nginx.conf && \
    touch /run/nginx.pid && \
    chown -R nginx:nginx /usr/share/nginx/html /var/cache/nginx /run

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Expose the desired port (default is 80 for Nginx)
EXPOSE 80

# Set the entrypoint to inject runtime env vars then start Nginx
ENTRYPOINT ["/entrypoint.sh"]
