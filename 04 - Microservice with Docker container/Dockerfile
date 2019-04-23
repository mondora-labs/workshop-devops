FROM maven as builder

WORKDIR /app

COPY pom.xml /app
RUN mvn dependency:go-offline

COPY . /app
RUN mvn package

FROM openjdk:slim

WORKDIR /app

COPY --from=builder /app/target/workshop-04.jar /app/

EXPOSE 8080
CMD java -jar /app/workshop-04.jar
