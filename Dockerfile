FROM eclipse-temurin:17.0.1_12-jre-alpine
MAINTAINER kir-dev@sch.bme.hu
COPY build/libs/cmsch.jar /opt/cmsch/
WORKDIR /opt/cmsch
ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=90", "-jar", "/opt/cmsch/cmsch.jar"]
EXPOSE 80
