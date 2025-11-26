FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/BRAVA.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "/app/BRAVA.jar", "org.springframework.boot.loader.JarLauncher"]