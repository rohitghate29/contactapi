# Use the OpenJDK 21 image as base
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Copy the source code
COPY src ./src

# Copy the Maven wrapper files if you are using it (optional)
COPY .mvn ./.mvn
COPY mvnw .

# Ensure the Maven wrapper script has execute permissions
RUN chmod +x ./mvnw

# Install Maven
RUN apk add --no-cache maven

# Build the project
RUN ./mvnw clean package -DskipTests

# Expose the port that your application will run on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/contactapi-0.0.1-SNAPSHOT.jar"]
