# For sake of simplicity we'll use ubuntu as a base image for building
FROM ubuntu as builder

ENV GRAAL_VERSION 1.0.0-rc15
ENV GRAAL_FILENAME graalvm-ce-${GRAAL_VERSION}-linux-amd64.tar.gz
ENV GRAALVM_HOME /usr/lib/graalvm

# Installing build time dependencies
RUN apt-get update && \
    apt-get install -y gcc libc6-dev zlib1g-dev curl maven openjdk-11-jdk

RUN curl -4 -L https://github.com/oracle/graal/releases/download/vm-${GRAAL_VERSION}/${GRAAL_FILENAME} -o /tmp/${GRAAL_FILENAME}

RUN tar -zxvf /tmp/${GRAAL_FILENAME} -C /tmp &&\
    mv /tmp/graalvm-ce-${GRAAL_VERSION} ${GRAALVM_HOME}

WORKDIR /app

# Prepare by downloading dependencies
ADD ./pom.xml /app
RUN mvn dependency:go-offline

# Adding source, compile and package into a fat jar
ADD src /app/src
RUN mvn package -Pnative

FROM debian:sid-slim

COPY --from=builder /app/target/*-runner /usr/share/app/

EXPOSE 8080
CMD ./usr/share/app/getting-started-runner
