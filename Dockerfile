FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY target/tm-auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
LABEL authors="rinugeorge"
ENTRYPOINT ["java", "-jar", "app.jar" ]