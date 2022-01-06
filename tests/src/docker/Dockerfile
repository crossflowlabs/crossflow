FROM openjdk:11

COPY . /app

WORKDIR /app

RUN sh -c 'touch /calcS.jar'
ENTRYPOINT ["java", "-jar", "calcS.jar"]
