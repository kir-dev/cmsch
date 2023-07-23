FROM eclipse-temurin:17-jre-alpine
MAINTAINER kir-dev@sch.bme.hu
COPY build/libs/cmsch.jar /opt/cmsch/
WORKDIR /opt/cmsch
ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=70", "-jar", "/opt/cmsch/cmsch.jar"]
EXPOSE 80
