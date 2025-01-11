FROM gradle:8.5-jdk21 AS BUILD

COPY build.gradle settings.gradle gradlew /home/gradle/project/
COPY gradle /home/gradle/project/gradle/
COPY api /home/gradle/project/api/
COPY core /home/gradle/project/core/

FROM openjdk:21-jdk-slim

COPY --from=BUILD /home/gradle/project/api/build/libs/api-0.0.1-SNAPSHOT.jar /app/app.jar

RUN ls -la /app/ && java -version

ENTRYPOINT ["java", "-jar", "/app/app.jar"]