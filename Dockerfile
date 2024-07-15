# Start with a base image containing Java runtime
FROM alpine/java:21-jdk

# The application's jar file
ARG JAR_FILE=target/eventhop.jar

# Copy the .env file to the container
COPY .env /app/.env

# Copy the application jar to the container
COPY ${JAR_FILE} app.jar

# Expose port 8080
EXPOSE 8080:8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
