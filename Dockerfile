# Build
FROM maven:3.8.5-openjdk-17 AS build

COPY . .

RUN mvn clean package -DskipTests


# Use a JDK 17 base image
FROM openjdk:17.0.1-jdk-slim

# Copy the packaged jar file into the container
COPY --from=build /target/schedulerapi-1.jar schedulerapi.jar

# Expose the port that your Spring Boot application uses (default is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java","-jar","schedulerapi.jar"]