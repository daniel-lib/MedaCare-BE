FROM maven:3.8.5-openjdk-17 AS build
COPY  . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/backend-0.0.2-SNAPSHOT.jar medacare-backend-0.0.2.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","medacare-backend-0.0.2.jar" ]
