FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} db-service.jar
ENTRYPOINT ["java","-jar","/db-service.jar"]