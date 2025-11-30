FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy any jar produced in target/ (useful when the jar name contains version/snapshot)
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
