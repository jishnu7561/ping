FROM openjdk:17-jdk

WORKDIR /app

COPY target/service-registry-0.0.1-SNAPSHOT.jar /app/service-registry.jar

EXPOSE 8761

CMD ["java", "-jar", "service-registry.jar"]