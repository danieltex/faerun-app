FROM openjdk:11-jre-slim-buster

ADD build/libs/*.jar app.jar
EXPOSE 5000

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
