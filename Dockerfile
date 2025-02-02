FROM openjdk:21-slim
LABEL authors="GEOGREEN-PLATFORM"
COPY target/file-server*.jar /file-server.jar
ENTRYPOINT ["java", "-jar", "/file-server.jar"]