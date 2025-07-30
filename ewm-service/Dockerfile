FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY ewm-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]