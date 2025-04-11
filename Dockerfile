FROM node:18.19.1 as build-ui

WORKDIR /app/stock-ui

# Copy Angular source code
COPY stock-ui/ ./

RUN npm cache clean --force
# Install dependencies and build the Angular app
RUN npm install
RUN rm -rf dist && npx ng build --output-path=dist --base-href /
# Stage 2: Build the Spring Boot backend and copy Angular into static/
FROM maven:3.9.7-eclipse-temurin-17 as build-backend

WORKDIR /app

# Copy backend source code
COPY pom.xml ./
COPY src ./src

# Copy the Angular build output into the backend static resources directory
COPY --from=build-ui /app/stock-ui/dist /app/src/main/resources/static/

# Build the Spring Boot app
RUN mvn clean install -DskipTests

# Stage 3: Final image to run the app
FROM openjdk:17-slim

WORKDIR /app

# Copy the Spring Boot jar from the build stage
COPY --from=build-backend /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]