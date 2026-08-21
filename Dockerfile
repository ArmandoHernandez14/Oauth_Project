# FROM eclipse-temurin:21-jre

# WORKDIR /app

# COPY target/oauth-server-0.0.1-SNAPSHOT.jar app.jar

# # Create the data directory
# RUN mkdir -p data

# # Copy the initial users file
# COPY src/main/resources/data/users.json data/users.json

# EXPOSE 8080

# ENTRYPOINT ["java","-jar","app.jar"]
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/oauth-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]