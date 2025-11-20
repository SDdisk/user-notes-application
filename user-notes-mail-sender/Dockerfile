FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY gradlew ./
COPY gradle ./gradle

RUN ./gradlew --no-daemon dependencies

COPY src ./src

RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]