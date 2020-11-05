FROM openjdk:11.0.4
EXPOSE 8080
WORKDIR /tmp
COPY build/libs/smart-home-hub-1.0-SNAPSHOT.jar app.jar
COPY ./firebase-adminsdk.json firebase-adminsdk.json
CMD java -jar app.jar