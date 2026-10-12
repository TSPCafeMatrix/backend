# ─── Stage 1: Build ───────────────────────────────────────────────
FROM eclipse-temurin:24-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:resolve -q
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# ─── Stage 2: Run ────────────────────────────────────────────────
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/target/cafe-lab-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]