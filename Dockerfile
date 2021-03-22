FROM openjdk:11-slim-buster

COPY target/dependency-jars /run/dependency-jars
ADD target/ec-kafka-szperacz.jar /run/application.jar

CMD java -jar run/application.jar
