FROM openjdk:17.0.2
ARG JAR_FILE=build/libs/artzip-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} artzip.jar
ENTRYPOINT ["java","-Dspring.config.use-legacy-processing=true","-Duser.timezone=\"Asia/Seoul\"","-jar","/artzip.jar"]