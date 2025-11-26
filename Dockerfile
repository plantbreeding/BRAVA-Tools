FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the already-built Spring Boot fat JAR from your host
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]