FROM openjdk:17.0.2
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} artzip.jar
ENTRYPOINT ["java","-Dspring.profile.active=dev","-jar","/artzip.jar"]