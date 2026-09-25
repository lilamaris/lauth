FROM eclipse-temurin:21-jre-noble

ARG JAR_FILE=app.jar

WORKDIR /app

COPY ${JAR_FILE} /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]