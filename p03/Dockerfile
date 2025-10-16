FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy any jar produced in target/ (useful when the jar name contains version/snapshot)
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
