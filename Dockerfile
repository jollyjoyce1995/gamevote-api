# Use a base image with Java and Maven installed
FROM maven:3.8.3-openjdk-17 as build

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml file to the image
COPY pom.xml .

ARG JDBC_DRIVER_GROUP_ID
ARG JDBC_DRIVER_ARTIFACT_ID
ARG JDBC_DRIVER_VERSION
ARG JDBC_DRIVER_GROUP_FOLDER
RUN JDBC_DRIVER_GROUP_FOLDER=


# Download the driver artifact
RUN mvn dependency:get -DgroupId=${JDBC_DRIVER_GROUP_ID} -DartifactId=${JDBC_DRIVER_ARTIFACT_ID} -Dversion=${JDBC_DRIVER_VERSION}
RUN export groupFolder=$(echo "${JDBC_DRIVER_GROUP_ID}" | sed 's/\./\//g'); mvn install:install-file -Dfile=/root/.m2/repository/$groupFolder/${JDBC_DRIVER_ARTIFACT_ID}/${JDBC_DRIVER_VERSION}/${JDBC_DRIVER_ARTIFACT_ID}-${JDBC_DRIVER_VERSION}.jar -DgroupId=at.tailor -DartifactId=driver -Dversion=1.0.0 -Dpackaging=jar

# Download dependencies to the local maven repository
RUN mvn dependency:go-offline

# Copy the rest of the source code to the image
COPY . .

# Build the application with Maven
RUN mvn package

# Use a base image with just Java installed
FROM openjdk:17-slim

# Set the working directory to /app
WORKDIR /app

# Copy the built application jar from the build stage
COPY --from=build /app/target/*.jar .

# Expose the application port
EXPOSE 8080

# Set the environment variable for the application to run
ENV JAVA_OPTS=""

# Start the application when the container starts
CMD java $JAVA_OPTS -jar *.jar