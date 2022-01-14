FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine
MAINTAINER kir-dev@sch.bme.hu
COPY build/libs/cmsch.jar /opt/cmsch/
WORKDIR /opt/cmsch
ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=98", "-jar", "/opt/cmsch/cmsch.jar"]
EXPOSE 80
