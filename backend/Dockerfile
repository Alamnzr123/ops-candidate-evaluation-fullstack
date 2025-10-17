FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# copy sample-project into build context so it's available to copy into final image
COPY sample-project /workspace/sample-project

# copy maven wrapper / pom first for caching
COPY backend/pom.xml backend/mvnw backend/.mvn* ./backend/
WORKDIR /workspace/backend
# copy rest of backend sources
COPY backend/ .

RUN chmod +x mvnw || true
RUN ./mvnw -B -DskipTests clean package

# Runtime image (JRE 21)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# copy jar produced by build stage
COPY --from=build /workspace/backend/target/*.jar ./app.jar

# copy sample-project into runtime image (optional; docker-compose volume can override)
COPY --from=build /workspace/sample-project /import/sample-project

EXPOSE 8080
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar" ]