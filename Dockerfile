FROM maven:3.9.4-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests
ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
FROM openjdk:21-jdk-slim
COPY --from=build /target/focusflow-0.0.1-SNAPSHOT.jar focusflow.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","focusflow.jar"]