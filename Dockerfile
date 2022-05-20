# syntax = docker/dockerfile:experimental

FROM  maven:3.8.5-eclipse-temurin-17 as build


WORKDIR /workspace/

COPY pom.xml .
COPY src src

COPY .git .git

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -e -DskipTests=true package


FROM openjdk:17.0.2-slim-buster


ENV app_name="rendezvous-server-jar-with-dependencies"

COPY --from=build /workspace/target/${app_name}.jar /app/${app_name}.jar

WORKDIR /app

EXPOSE 8007

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/urandom -jar ${app_name}.jar"]
