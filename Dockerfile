# Builder stage
FROM amazoncorretto:21 AS builder
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Final stage
FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
EXPOSE ${SPRING_DOCKER_PORT}
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
