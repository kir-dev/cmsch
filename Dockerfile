FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine
MAINTAINER gerviba@sch.bme.hu
COPY build/libs/g7.jar /opt/g7web/
WORKDIR /opt/g7web
ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=98", "-jar", "/opt/g7web/g7.jar"]
EXPOSE 80