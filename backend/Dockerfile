FROM eclipse-temurin:21-jre-alpine
MAINTAINER kir-dev@sch.bme.hu
COPY build/libs/cmsch.jar /opt/cmsch/
WORKDIR /opt/cmsch

RUN java -Djarmode=tools -jar cmsch.jar extract --destination application
RUN rm cmsch.jar

WORKDIR /opt/cmsch/application
RUN java "-Dspring.profiles.include=prewarm" "-XX:ArchiveClassesAtExit=application.jsa" "-XX:+UseSerialGC" "-XX:MaxRAMPercentage=75" "-Dspring.context.exit=onRefresh" -jar cmsch.jar

ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:SharedArchiveFile=application.jsa", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-jar", "/opt/cmsch/application/cmsch.jar"]
EXPOSE 80
