FROM java:8

MAINTAINER Michael Ruggiero<mjamesruggiero@gmail.com>

CMD ["sudo", "apt-get", "update"]

ADD target/swanson-0.1.0-SNAPSHOT-standalone.jar /srv/swanson.jar

EXPOSE 8080

CMD ["java", "-jar", "/srv/swanson.jar"]                         
