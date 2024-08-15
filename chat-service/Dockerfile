FROM openjdk:17-jdk

WORKDIR /app

COPY target/chat-service-0.0.1-SNAPSHOT.jar /app/chat-service.jar

EXPOSE 8085

CMD ["java", "-jar", "chat-service.jar"]