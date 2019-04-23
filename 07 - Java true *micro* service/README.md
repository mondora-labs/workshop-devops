# Reducing Java services footprint

- [Reducing Java services footprint](#reducing-java-services-footprint)
  - [Prerequisites](#prerequisites)
  - [Problem](#problem)
  - [GraalVM and native-image](#graalvm-and-native-image)
  - [Sample project](#sample-project)
    - [Benchmarks](#benchmarks)
    - [Conclusion](#conclusion)

## Prerequisites

- Java SDK 1.8+
- Maven build tool
- GraalVM installed

**Attention:** `JAVA_HOME` and `GRAALVM_HOME` system environment variables must
point to the corresponding installation folders, for example:

| Environment variable | Value                                                           |
| -------------------- | --------------------------------------------------------------- |
| JAVA_HOME            | /Users/\<your-username>/.sdkman/candidates/java/current         |
| GRAALVM_HOME         | /Users/\<your-username>/.sdkman/candidates/java/1.0.0-rc-15-grl |

To efficiently manage Java and build tools versions you can use [SDKMAN!](https://sdkman.io/).

## Problem

You already exposed your first Java service and optimized disk size utilization
with a multistage Docker build, but at runtime the JVM is eating many megabytes
of your RAM.

Here a typical (stripped down) `docker stats` output at **application startup**:

```bash
CONTAINER ID       CPU %       MEM USAGE / LIMIT      MEM %        PIDS
d8964d30c8b3       0.16%       142MiB / 512MiB        27.74%       21
```

- above `d8964d30c8b3` container is a simple Spring Boot application providing a
  simple REST endpoint and Swagger documentation.

That can be considered a **non**-problem when exposing a couple of services,
since RAM is cheap nowadays, but if you embrace the μ-services philosophy is not
that remote to expose and manage tens or hundreds of independents services.

So:

```bash
(JVM overhead + service memory consumption) * _n_ of μ-services = tons of RAM (!!!)
```

## GraalVM and native-image

There are many frameworks and solution to tackle this kind of problem, all are
building on top the GraalVM.

> GraalVM is a universal virtual machine for running applications written in
> JavaScript, Python, Ruby, R, JVM-based languages like Java, Scala, Kotlin,
> Clojure, and LLVM-based languages such as C and C++. GraalVM removes the
> isolation between programming languages and enables interoperability in a
> shared runtime. It can run either standalone or in the context of OpenJDK,
> Node.js, Oracle Database, or MySQL.

With one particular interesting feature:

> Native images compiled with GraalVM ahead-of-time improve the startup time and
> reduce the memory footprint of JVM-based applications.

- [Spring Boot](https://github.com/oracle/graal/issues/348)
- [Micronaut Framework](https://docs.micronaut.io/latest/guide/index.html#graalServices)
- [Quarkus](https://quarkus.io/guides/building-native-image-guide)

Native image is currently a WIP features and it comes with some challenges.
Quoting Micronaut documentation:

> GraalVM support (like GraalVM itself) is still in the incubating phase.
> Third-party library support is hit and miss and the Micronaut team are still
> ironing out all of the potential issues. Don’t hesitate to report issues
> however and gradually over time the support will become more stable.

But is always a good thing to have a sneak peak at what the future can provide.
Quarkus project offers the most smooth DX.

In fact to enable the AOT compilation you only need to enable the provided
`native` profile:

```bash
mvn package -Pnative
```

## Sample project

The project offer a basic implementation of the 4 arithmetic operations (sum,
subtraction, multiplication and division) for pre-1970 UK prices.

> Under the old money system of UK, before 1970, there were 12 pence in a
> shilling and 20 shillings in a pound. Thus, a price in th Old UK money system
> was expressed in Pounds, Shillings and Pence.

The project is structured as follow:

```bash
├── main
│   ├── java
│   │   └── org
│   │       └── acme
│   │           └── quickstart
│   │               ├── OldFashionPound.java             # Model
│   │               ├── OldFashionPoundController.java   # Controller
│   │               └── OldFashionPoundService.java      # Business logic
│   └── resources
│       └── META-INF
│           └── resources
│               └── index.html
└── test
    └── java
        └── org
            └── acme
                └── quickstart
                    └── OldFashionPoundServiceTest.java  # Unit tests
```

You can test the [sum](http://localhost:8080/oldFaschionPound/sum) endpoint with
this request body:

```json
{
  "amount1": {
    "sterline": 5,
    "scellini": 17,
    "pence": 8
  },
  "amount2": {
    "sterline": 3,
    "scellini": 4,
    "pence": 10
  }
}
```

You should receive an HTTP 200 with the following response body:

```json
{
  "pence": 6,
  "scellini": 2,
  "sterline": 9
}
```

A sample `Dockerfile` is provided to have our binary exposed:

> For sake of simplicity we'll use ubuntu as a base image for building

### Benchmarks

```bash
➜ docker images
REPOSITORY                       TAG           IMAGE ID            SIZE
quarkus-multistage               latest        9f7fb08ac020        485MB
quarkus-multistage-native        latest        7b4cd1bff13e        91.4MB

➜ docker run -d -p 8081:8080 -m 512m quarkus-multistage
952bd86dda41baa6faa90b4b581e762a30555fd9127473c5c6d0efe84f2aec1e

➜ docker logs 952bd86dda41baa6faa90b4b581e762a30555fd9127473c5c6d0efe84f2aec1e
2019-04-23 10:52:32,619 INFO  [io.quarkus] (main) Quarkus 0.13.3 started in 1.095s. Listening on: http://0.0.0.0:8080
2019-04-23 10:52:32,644 INFO  [io.quarkus] (main) Installed features: [cdi, resteasy, resteasy-jsonb]

➜ docker run -d -p 8080:8080 -m 512m quarkus-multistage-native
06a0c39da10e447d6d46a215928e78fb9227572ed110b6c838108184790b4eb7

➜ docker logs 06a0c39da10e447d6d46a215928e78fb9227572ed110b6c838108184790b4eb7
2019-04-23 10:52:45,391 INFO  [io.quarkus] (main) Quarkus 0.13.3 started in 0.003s. Listening on: http://0.0.0.0:8080
2019-04-23 10:52:45,391 INFO  [io.quarkus] (main) Installed features: [cdi, resteasy, resteasy-jsonb]
```

| Image type                | Image disk size | Memory used on startup | Startup time |
| ------------------------- | --------------- | ---------------------- | ------------ |
| quarkus-multistage        | 485MB           | 44.47MiB               | 1.095s       |
| quarkus-multistage-native | 91.4MB          | 1.762MiB               | 0.003s       |

### Conclusion

By using Quarkus you are already beneficing from a lightweight application runtime.

`native-image` tools provided by `GraalVM` can enable new scenarios where truly
high density compute clusters are possible... without sacrificing developer
proficiency with the Java language.
