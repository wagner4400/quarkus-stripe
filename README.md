<p align="center">
  <img src="logo.png" alt="Logo" width="80" height="80">
  <h3 align="center">🍽️ TastefulPantry 🍽️</h3>
  <p align="center">
    Social Cookbook Platform with Quarkus
    <br />
    <a href="#overview"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/wagner4400/TastefulPantry/issues">Report Bug</a>
    ·
    <a href="https://github.com/wagner4400/TastefulPantry/issues">Request Feature</a>
  </p>
</p>

## 📚 Table of Contents
* [Overview](#overview)
* [Roadmap](#roadmap)
* [Running the Application](#running-the-application)
* [Packaging and Running the Application](#packaging-and-running-the-application)
* [Creating a Native Executable](#creating-a-native-executable)
* [Related Guides](#related-guides)
* [Provided Code](#provided-code)
* [License](#license)
* [Contact](#contact)

## 🌐 Overview
Develop a social platform where users can create, share, and discover recipes. Users can create their custom cookbooks, follow friends, and share their culinary creations. This project will showcase your skills in Java, Quarkus, REST APIs, database interaction, and potentially some social networking features.

## 🚀 Roadmap
- [ ] **User Authentication and Profiles:** Implement user authentication and registration. Users can create profiles with details like name, bio, profile picture, etc.
- [ ] **Recipe Management:** Create, edit, and delete recipes. Each recipe includes: Title, Description, Ingredients, Instructions, Tags (e.g., vegetarian, vegan, Italian). Cooking time, Difficulty level, Serving size.
- [ ] **Cookbook Creation:** Users can create custom cookbooks to organize their recipes. Add recipes to their cookbooks. Make cookbooks public or private.
- [ ] **Social Features:** Follow other users. Like, comment on, and share recipes. Discover recipes from followed users and trending recipes.
- [ ] **Search and Filter:** Search for recipes based on title, ingredients, tags, etc. Filter recipes by cuisine, dietary preferences, etc.
- [ ] **Sharing:** Share recipes and cookbooks with friends or publicly. Generate shareable links for private sharing.
- [ ] **Notification System:** Notify users of new followers, likes, comments, and recipe updates.
- [ ] **Favorites and History:** Allow users to mark recipes as favorites. View history of recently viewed recipes.

## Running the Application in Dev Mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/tastefulpantry-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and Jakarta Persistence
- Flyway ([guide](https://quarkus.io/guides/flyway)): Handle your database schema migrations
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, Jakarta Persistence)
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- JDBC Driver - MySQL ([guide](https://quarkus.io/guides/datasource)): Connect to the MySQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)



### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)



## 📜 License
[![CC0](https://licensebuttons.net/p/zero/1.0/88x31.png)](https://creativecommons.org/publicdomain/zero/1.0/)

## 📞 Contact
Wagner Souza Ramalho - wagner4400@gmail.com