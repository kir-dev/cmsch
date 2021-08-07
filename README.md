G7 Web Backend
===

## Build docker

```bash
  docker build -t g7-web-backend .
```

## Publish

```bash
  docker login registry.k8s.sch.bme.hu
  docker image tag g7-web-backend:latest registry.k8s.sch.bme.hu/g7-web/g7-web-backend:latest
  docker image push registry.k8s.sch.bme.hu/g7-web/g7-web-backend
```

## Run (you can start here)

For develpment:

```bash
  docker run --rm -p 8080:80 \
        -e AUTHSCH_CLIENT_ID=20_CHARS \
        -e AUTHSCH_CLIENT_KEY=80_CHARS \
        -e PROFILE_SALT=RANDOM_STRING \
        g7-web-backend
```

or from the registry: **YOU MIGHT PROBABLY WANT TO START WITH THIS**

```bash
  docker pull registry.k8s.sch.bme.hu/g7-web/g7-web-backend
  docker run --rm -p 8080:80 \
        -e AUTHSCH_CLIENT_ID=20_CHARS \
        -e AUTHSCH_CLIENT_KEY=80_CHARS \
        -e PROFILE_SALT=RANDOM_STRING \
        registry.k8s.sch.bme.hu/g7-web/g7-web-backend
```

## Where to start?

- Api docs: BASE_URL/swagger-ui.html
- Admin UI: BASE_URL/admin/control/basics
- API: BASE_URL/api/... (see swagger for more)