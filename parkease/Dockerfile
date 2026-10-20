FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/parkease-0.0.1-SNAPSHOT.jar /app/app.jar
RUN mkdir -p /app/data

ENV PORT=8081
ENV DB_PATH=/app/data/parkease
EXPOSE 8081
VOLUME ["/app/data"]

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
