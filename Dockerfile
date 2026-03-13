FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

MAINTAINER kg

COPY target/githubProject-0.0.1-SNAPSHOT.jar githubProject-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","githubProject-0.0.1-SNAPSHOT.jar"]