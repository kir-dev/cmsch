.PHONY: all build push-staging push-release

all:
	echo "build|push-staging|push-release"

build:
	./gradlew clean build
	docker build --platform=linux/amd64 -t cmsch .

push-staging:
	docker image tag cmsch:latest harbor.sch.bme.hu/org-golyakorte/cmsch:staging
	docker image push harbor.sch.bme.hu/org-golyakorte/cmsch:staging

push-release:
	docker image tag cmsch:latest harbor.sch.bme.hu/org-golyakorte/cmsch:release
	docker image push harbor.sch.bme.hu/org-golyakorte/cmsch:release
