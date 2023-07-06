Setting up this Env.
===

1. Set up the configs in `docker-compose-keycloak.yaml` labeled as 'TODO'
2. Start keycloak and postgres
    ```bash
        docker compose -f docker-compose-keycloak.yaml up --build
    ```
3. Log in into the keycloak instance http://localhost:8081/auth/admin/
4. Import or create a realm.
5. If you create one: 
    - Create a new client with confidential access type, then use the `Credentials` menu to get the client secret.
    - Add the `groups` custom role mapping. (Mappers -> Add Builtin -> select groups)
    - Add the `superuser` role to the superuser group.
6. Add your user to the `superuser` group. 
7. Create other users...
8. Set up the `docker-compose-prod.yaml` file. Replace the urls and set other settings.
9. Build and start the frontend and backend
    ```bash
        docker compose -f docker-compose-prod.yaml up --build
    ```
10. Login at `BACKEND_URL/oauth2/authorization/keycloak`
11. Have fun!
