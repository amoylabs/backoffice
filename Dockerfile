#
# Build stage
#
FROM gradle:6.8.3-jdk15 AS build
ADD . /home/gradle/project/
WORKDIR /home/gradle/project/
RUN gradle build

#
# Package stage
#
FROM adoptopenjdk/openjdk15:jdk-15.0.2_7
RUN mkdir /opt/app
COPY --from=build /home/gradle/project/build/back-office-web/libs/*.jar /opt/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=qa", "/opt/app/app.jar"]
