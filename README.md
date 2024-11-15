CMSch web backend and frontend monorepo
===

<a href="https://cmsch.vercel.app"><img src="https://therealsujitk-vercel-badge.vercel.app/?app=cmsch&style=for-the-badge"></a>

## Build docker

```bash
  ./gradlew clean bootBuildImage --imageName=<your registry>/cmsch:release
```

## Run

```bash
  ./gradlew bootRun --args='--spring.profiles.include=test,internal'
```

## Enable profiling

```bash
  ./gradlew -Dorg.gradle.jvmargs="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -XX:+UseSerialGC" clean bootRun --args='--spring.profiles.include=test,internal'
```

## Publish

Use your authsch details for docker login. Tag `rc` for staging (release candidate) and tag release for release.

```bash
  docker login harbor.sch.bme.hu
  
  # Release candidate
  docker image tag cmsch:latest harbor.sch.bme.hu/org-kir-dev/cmsch:rc
  docker image push harbor.sch.bme.hu/org-kir-dev/cmsch:rc
  
  # Release (you can use versions like ':major.minor.build' as well)
  docker image tag cmsch:latest harbor.sch.bme.hu/org-kir-dev/cmsch:release
  docker image push harbor.sch.bme.hu/org-kir-dev/cmsch:release
```

## Deploy to Kubernetes

### Prerequisites
- Install Kubectl
- Install Helm

### Deploy the application

- Create a copy of helm/cmsch/values.yaml and modify the values for your needs; you can delete the properties you don't modify to make the config cleaner
- **Select the correct Kubernetes context:** `kubectl config use-context <context>`
- Run `helm upgrade --install cmsch-<instance name> --values <path-to-your.yaml> ./helm/cmsch`
- If you need to change a value, or update the config, run the command above and everything updates automagically
- If you want to delete the instance, run `helm delete cmsch-<instance name>`


## Run (you can start here)

For development:

```bash
  docker run --rm -p 8080:80 \
        -e AUTHSCH_CLIENT_ID=20_CHARS \
        -e AUTHSCH_CLIENT_KEY=80_CHARS \
        -e PROFILE_SALT=RANDOM_STRING \
        -e SYSADMINS=YOUR_AUTH_SCH_UUID \
        cmsch
```

or from the registry: **YOU MIGHT PROBABLY WANT TO START WITH THIS**

```bash
  docker pull harbor.sch.bme.hu/org-kir-dev/cmsch
  docker run --rm -p 8080:80 \
        -e AUTHSCH_CLIENT_ID=20_CHARS \
        -e AUTHSCH_CLIENT_KEY=80_CHARS \
        -e PROFILE_SALT=RANDOM_STRING \
        -e SYSADMINS=YOUR_AUTH_SCH_UUID \
        harbor.sch.bme.hu/org-kir-dev/cmsch
```

## Where to start?

- Api docs: BASE_URL/swagger-ui.html
- Admin UI: BASE_URL/admin/control/basics
- API: BASE_URL/api/... (see swagger for more)

## Required apps

You must install:

- Node v20
- Yarn v1.22.17
- optional: IDEA

## Application local properties

Create an application-local.properties file in the `src/main/resources/config` folder, 
and fill the file with these configurations (using your credentials): 

```properties
spring.security.oauth2.client.registration.authsch.client-id=<insert the shorter key>
spring.security.oauth2.client.registration.authsch.client-secret=<insert the long key>
spring.security.oauth2.client.registration.google.client-id=<google client-id>
spring.security.oauth2.client.registration.google.client-secret=<google client-secret>
hu.bme.sch.cmsch.startup.sysadmins=<your pekId>
cmsch.website-default-url=http://<your ip>:8080/
hu.bme.sch.cmsch.login.googleAdminAddresses=<your email address>
logging.level.web=DEBUG
```

Your pekId can be found in the console log of the Spring app when signing in with AuthSCH. The `cmsch.website-default-url`
property's IP address needs to be either `localhost` or the IP of your current device running your Spring app on your network.

Once created, edit the `CMSchApplication` Run Configuration's Spring Boot Active Profiles to use (see image down below)

- `local,test` if you want test data in the database also
- `local` if you don't

## Set up push notifications

1. Enable the push notification component on the backend.
2. Create a Firebase project and make sure Firebase Cloud Messaging is enabled by navigating to `Run` > `Messaging`.

### Backend setup
1. Navigate to the Firebase Console of your project and open `Project Settings` > `Service accounts` 
2. Click on `Generate new private key` and download the .json file
3. If you are working locally set the value of `hu.bme.sch.cmsch.google.service-account-key` property to the contents of the JSON file
4. If you are setting up the application inside docker set `FIREBASE_SERVICE_ACCOUNT_KEY` to the contents of the JSON file

### Frontend setup
1. Navigate to the Firebase Console of your project and open `Project Settings` > `General`
2. Scroll down and create a __Web App__ if there is no app already by clicking `Add app`
3. Find the values of `apiKey, projectId, appId, messagingSenderId` and set the `FIREBASE_*` properties in .env
4. Navigate to `Project Settings` > `Cloud Messaging` and scroll down to `Web Push certificates`
5. If there is no key, click on `Generate key pair`. Copy the value from `Key pair` column and set `VITE_FIREBASE_WEB_PUSH_PUBLIC_KEY` to it.

## Sponsors

<a href="https://vercel.com?utm_source=kir-dev&utm_campaign=oss"><img src="client/public/img/powered-by-vercel.svg" height="46" /></a>
