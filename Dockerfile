FROM openjdk:17
COPY target/file-management-0.0.1-SNAPSHOT.jar file-management-0.0.1-SNAPSHOT.jar
ADD src/main/resources/docker.properties /app/docker.properties
ENTRYPOINT ["java","-jar", "-Dspring.config.location=file:///app/docker.properties", "/file-management-0.0.1-SNAPSHOT.jar"]