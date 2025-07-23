FROM eclipse-temurin:24-jre-alpine
MAINTAINER kir-dev@sch.bme.hu
COPY build/libs/cmsch.jar /opt/cmsch/
WORKDIR /opt/cmsch

RUN java -Djarmode=tools -jar cmsch.jar extract --destination application
RUN rm cmsch.jar

WORKDIR /opt/cmsch/application
RUN java "-Dspring.profiles.include=prewarm" "-XX:ArchiveClassesAtExit=application.jsa" "-XX:+UnlockExperimentalVMOptions" "-XX:+UseContainerSupport" "-XX:+UseSerialGC" "-XX:MaxRAMPercentage=90" "-Dspring.context.exit=onRefresh" -jar cmsch.jar

ENTRYPOINT ["java", "-Dspring.profiles.include=docker", "-XX:SharedArchiveFile=application.jsa", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=70", "-jar", "/opt/cmsch/application/cmsch.jar"]
EXPOSE 80
