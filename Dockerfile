FROM adoptopenjdk/openjdk11:alpine-slim

EXPOSE 8080
WORKDIR /tmp
COPY build/libs/smart-home-hub-1.0-SNAPSHOT.jar app.jar
RUN mkdir credentials
CMD java -jar app.jar -Ddefault.feed.cups=4