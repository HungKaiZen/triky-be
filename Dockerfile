FROM openjdk:17

COPY target/triky-be-0.0.1-SNAPSHOT.jar backend-service.jar

ENTRYPOINT ["java", "-jar", "/backend-service.jar"]

EXPOSE 8080
