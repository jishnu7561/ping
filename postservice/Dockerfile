FROM openjdk:17-jdk

WORKDIR /app

COPY target/post-service-0.0.1-SNAPSHOT.jar /app/post-service.jar

EXPOSE 8084

CMD ["java", "-jar", "post-service.jar"]