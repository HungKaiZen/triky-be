#FROM openjdk:17
#
#ARG JAR_FILE=target/*.jar
#
#COPY ${JAR_FILE} backend-service.jar
#
#ENTRYPOINT ["java", "-jar", "backend-service.jar"]
#
#EXPOSE 8080


FROM maven:3.9.6-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /app/target/*.jar backend-service.jar
ENTRYPOINT ["java", "-jar", "backend-service.jar"]
