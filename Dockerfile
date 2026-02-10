# Stage 1: Build the WAR file using Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Deploy the WAR file to TomEE
FROM tomee:9.1.3-plus

# Remove default ROOT application
RUN rm -rf /usr/local/tomee/webapps/ROOT

# Copy the WAR file from the build stage
COPY --from=build /app/target/Assignment2_J2EE.war /usr/local/tomee/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# TomEE will auto-deploy the WAR file
CMD ["catalina.sh", "run"]
