FROM eclipse-temurin:17
RUN mkdir /opt/app
COPY ./usermanagement/target/usermanagement-1.0-SNAPSHOT.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar"]