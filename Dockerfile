FROM openjdk:11.0.6

COPY core/target/dependency-jars /run/dependency-jars
ADD core/target/map-matching-core.jar /run/application.jar

CMD java -jar run/application.jar
