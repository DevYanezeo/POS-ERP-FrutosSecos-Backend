# Usar una imagen base con Java y Maven
FROM maven:3.8.7-openjdk-17 AS build
WORKDIR /app

# Copiar los archivos de Maven y el código fuente
COPY pom.xml .
COPY src ./src

# Compilar el proyecto y crear el JAR
RUN mvn clean package -DskipTests

# Usar una imagen ligera de Java para producción
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

