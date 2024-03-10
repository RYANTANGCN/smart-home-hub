FROM eclipse-temurin:11-jre-alpine

EXPOSE 8080
WORKDIR /tmp
COPY build/libs/smart-home-hub-1.0-SNAPSHOT.jar app.jar
RUN mkdir credentials
CMD java -Ddefault.feed.cup=4 -jar app.jar