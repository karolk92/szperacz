FROM sonatype.itl.waw.pl:8083/repository/docker-private/openjdk:11-slim-buster

COPY core/target/dependency-jars /run/dependency-jars
ADD core/target/map-matching-core.jar /run/application.jar

CMD java -jar run/application.jar
