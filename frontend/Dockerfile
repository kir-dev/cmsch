# Use a Node.js base image for building the application
FROM docker.io/node:24-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package.lock (or package-lock.json) to the working directory
COPY package.json package.lock ./

# Install dependencies
RUN npm ci

# Copy the rest of the application code to the working directory
COPY . .

# Build the application
RUN npm run build

# Use a separate Nginx base image for serving the built application
FROM docker.io/nginx:alpine

# Copy the built application files to the Nginx web server directory
COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose the desired port (default is 80 for Nginx)
EXPOSE 80

# Set the command to start the Nginx server
CMD ["nginx", "-g", "daemon off;"]
