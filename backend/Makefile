.PHONY: all build push-rc push-r

all:
	echo "build|push-rc|push-r"

build:
	./gradlew clean build
	docker build --platform=linux/amd64 -t cmsch .

push-rc:
	docker image tag cmsch:latest harbor.sch.bme.hu/org-kir-dev/cmsch:rc
	docker image push harbor.sch.bme.hu/org-kir-dev/cmsch:rc

push-r:
	docker image tag cmsch:latest harbor.sch.bme.hu/org-kir-dev/cmsch:release
	docker image push harbor.sch.bme.hu/org-kir-dev/cmsch:release
