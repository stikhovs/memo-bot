FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/memo-bot-0.0.1-SNAPSHOT.jar .

VOLUME /app/data

CMD ["java", "-jar", "memo-bot-0.0.1-SNAPSHOT.jar"]