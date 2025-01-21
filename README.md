CMSch web backend and frontend monorepo
===

<a href="https://cmsch.vercel.app"><img src="https://therealsujitk-vercel-badge.vercel.app/?app=cmsch&style=for-the-badge"></a>

## Contributing

Follow the [instructions by Samu](https://gist.github.com/Tschonti/4397e43fef11895235e25c46ae0ed65e#workflow-), with the
following additions:

- This project uses squash merging, meaning pull requests result in a single commit on the main branch.
If you find something, unrelated to the feature you are working on, that could be improved, **consider opening another PR** instead of adding the changes to the original one.
This makes reviewing the pull request, searching through the history and reverting changes easier.
- **Follow [How to Write a Git Commit Message](https://cbea.ms/git-commit/)**. As this project is not versioned, **don't use [conventional commits](https://conventionalcommits.org)** ("feat (thing): add thing").
- If there are conflicts with the main branch, **rebase** your feature branch onto `origin/staging` (`git fetch && git rebase origin/staging`), fix the conflicts, *commit*, then force push the updated branch (`git push --force`).
- To make it easier to review your pull request, consider cleaning up the commits with `git rebase --interactive`. You can also use `git commit --amend` to add the currently staged changes to the last commit instead of creating a new commit. Both of these require force pushing to allow modifying published history.

## Build and deployment

### Build the frontend

Copy the `.env.example` file to `.env` and fill it with the required data.

```bash
  yarn run build
```

### Build the backend OCI image

```bash
  ./gradlew clean bootBuildImage --imageName=<your registry>/cmsch:release
```

### Deploy to Kubernetes

#### Prerequisites

- Install Kubectl
- Install Helm

#### Deploy the application

- Create a copy of helm/cmsch/values.yaml and modify the values for your needs; you can delete the properties you don't
  modify to make the config cleaner
- **Select the correct Kubernetes context:** `kubectl config use-context <context>`
- Run `helm upgrade --install cmsch-<instance name> --values <path-to-your.yaml> ./helm/cmsch`
- If you need to change a value, or update the config, run the command above and everything updates automagically
- If you want to delete the instance, run `helm delete cmsch-<instance name>`

### Set up push notifications

1. Enable the push notification component on the backend.
2. Create a Firebase project and make sure Firebase Cloud Messaging is enabled by navigating to `Run` > `Messaging`.

#### Backend setup

1. Navigate to the Firebase Console of your project and open `Project Settings` > `Service accounts`
2. Click on `Generate new private key` and download the .json file
3. If you are working locally set the value of `hu.bme.sch.cmsch.google.service-account-key` property to the contents of
   the JSON file
4. If you are setting up the application inside docker set `FIREBASE_SERVICE_ACCOUNT_KEY` to the contents of the JSON
   file

#### Frontend setup

1. Navigate to the Firebase Console of your project and open `Project Settings` > `General`
2. Scroll down and create a __Web App__ if there is no app already by clicking `Add app`
3. Find the values of `apiKey, projectId, appId, messagingSenderId` and set the `FIREBASE_*` properties in .env
4. Navigate to `Project Settings` > `Cloud Messaging` and scroll down to `Web Push certificates`
5. If there is no key, click on `Generate key pair`. Copy the value from `Key pair` column and set
   `VITE_FIREBASE_WEB_PUSH_PUBLIC_KEY` to it.

## Local Development Environment

### Database

The backend supports both PostgreSQL and H2.
While it is a lot easier to develop under H2, you might want to test the application with Postgres too.
Possibly the easiest way to do this is to run it via Docker.
Important, You need to add `postgres` to the Spring profiles.
This command starts an instance that works with the default backend configuration:

```bash
docker run -p5432:5432 --name cmsch-postgres -e POSTGRES_PASSWORD=psqlpw -e POSTGRES_USER=psqluser -e POSTGRES_DB=cmsch -d postgres:17-alpine
```

### Backend

Open the monorepo in Intellij and run `CMSchApplication`.
You can seed the database by editing the run configuration of
`CMSchApplication` and adding `test` to the active profiles.

You can run the application manually through the CLI, but you miss out on a lot of features that ease development.
You can also seed the database with some test data by setting the active Spring profile to `test`
Just simply open a terminal in the `backend` folder and run

```bash
./gradlew bootRun --args='--spring.profiles.active=test,local'
```

### Local properties for development

Create a file named `application-local.properties` in the `src/main/resources/config` folder,
and specify these configurations (using your credentials):

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

Your pekId can be found in the console log of the Spring app when signing in with AuthSCH.
The`cmsch.website-default-url`property's IP address needs
to be either `localhost` or the IP of your current device running your Spring app on your network.

Once created, edit the `CMSchApplication` Run Configuration's Spring Boot Active Profiles to use (see image down below)

- `local,test` if you want test data in the database also
- `local` if you don't

### Frontend

Copy the `.env.example` file to `.env` and fill it with the required data.
You can leave the defaults for local development.

Open a terminal in the `frontend` folder and pull the packages

```bash
yarn
```

then start the frontend development server

```bash
yarn start
```
