# Domain Gateway Demo

This repository contains the codebase that was used in the article
[Building Your Domain Gateway With OpenAPI]() in the blog [yonatankarp.com](https://yonatankarp.com).

The repository contains the following models:

- **Domain Gateway** - a facade for the domain that calls all the services
  belong to the domain.
- **Hello Service** - a service responsible to get a name and answer with
  `Hello, <NAME>!`.
- **Goodbye Service** - a service responsible to get a name and answer with
  `Goodbye <NAME>!`.

## How to run

The services can run using the `docker compose` file supplied in the root
directory of this repository by running the command:

```shell
$ ./gradlew assemble && docker compose up --build
```

Once all the services are up and running you can test the integration between
them by calling the services as follow:

```shell
$ curl 'localhost:8080/hello/Yonatan'
```

Response example:

```json
{"value":"Hello, Yonatan!"}
```
For the goodbye endpoint, run the following:

```shell
$ curl 'localhost:8080/goodbye/Yonatan'
```

Response example:

```json
{"value":"Goodbye Yonatan!"}
```

## Built With

- [OpenJdk 17](https://openjdk.java.net/projects/jdk/17/)
- [Kotlin](https://kotlinlang.org/) 
- [Kotlin Coroutine](https://kotlinlang.org/docs/coroutines-overview.html)
- [SpringBoot 3.x.x](https://spring.io/projects/spring-boot) - The web framework used
- [Gradle Kotlin DSL](https://gradle.org/) - Dependency Management

## Authors

- **Yonatan Karp-Rudin** - *Initial work* - [yonatankarp](https://github.com/yonatankarp)
