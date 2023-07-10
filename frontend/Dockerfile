# Use a Node.js base image for building the application
FROM node:16-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and yarn.lock (or package-lock.json) to the working directory
COPY package.json yarn.lock ./

# Install dependencies
RUN yarn install

# Copy the rest of the application code to the working directory
COPY . .

# Build the application
RUN yarn build

# Use a separate Nginx base image for serving the built application
FROM nginx:alpine

# Copy the built application files to the Nginx web server directory
COPY --from=build /app/build /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose the desired port (default is 80 for Nginx)
EXPOSE 80

# Set the command to start the Nginx server
CMD ["nginx", "-g", "daemon off;"]
