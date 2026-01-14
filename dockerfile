FROM maven:3.9-eclipse-temurin-17 AS base

RUN mkdir -p /app
WORKDIR /app

ADD pom.xml /app

RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never"]

ADD . /app


FROM base AS dev

RUN mvn package -DskipTests

EXPOSE 8080

CMD ["/bin/sh", "-c", "java -jar target/*.jar"]


FROM base AS test

EXPOSE 8080

CMD ["mvn", "test"]