[![codecov](https://codecov.io/gh/danieltex/faerun-app/branch/main/graph/badge.svg?token=DKMRP2QKPC)](https://codecov.io/gh/danieltex/faerun-app)

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/danieltex/faerun-app">
    <img src="images/cave-lake-4805991_960_720.jpg" alt="Logo" width="200" height="200">
  </a>

  <h3 align="center">Faerun Balance</h3>

  <p align="center">
    An API to manage water pockets storage and loans between them
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project



### Built With

* [Gradle](https://gradle.org/) and [Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
* [Spring Boot 2.5.1](https://spring.io/projects/spring-boot)
* [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi)
* [JDK 11](https://www.oracle.com/java/technologies/javase/jdk11-readme.html)
* [Kotlin 1.5](https://kotlinlang.org/)
* [MySQL 8.0](https://dev.mysql.com/)
* [H2 Database](https://www.h2database.com/html/main.html) (for integration testing)
* [Docker](https://www.docker.com/)
* [JaCoCo 0.8.7](https://github.com/jacoco/jacoco)
* [Codecov](https://about.codecov.io/) for code coverage reports triggered by Github Actions
* [Mockito-Kotlin](https://github.com/mockito/mockito-kotlin/)



<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.


### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/danieltex/faerun-app.git
   ```
2. Install Java JDK 11+
   
   Follow the installation guide for your OS at [Overview of JDK Installation](https://docs.oracle.com/en/java/javase/11/install/overview-jdk-installation.html)



<!-- USAGE EXAMPLES -->
## Usage

### Run the full application through docker-compose:
1. Building the source

   `./gradlew clean build`

2. Build the docker image:

   `docker build -t faerunapp .`

3. Running
   
   Start the database and app with docker compose:
   
   `docker-compose up`

### Run the database with docker-compose and start the application manually:
1. Start the database

   `docker-compose up db`

2. Build and run the application with gradle or start the `FaerunAppApplicationKt` class through your IDE of preference

   `./gradlew bootRun`

The application will start on the port `5000`

### Examples

1. Create a new water pocket

```bash
curl --location --request POST 'http://localhost:5000/water-pockets' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "name": "Itaipu",
    "storage": 62200000.0
}'
```

Response:
```json
{
    "name": "Itaipu",
    "storage": 62200000.0,
    "id": 1
}
```

More examples can be seen at the API Documentation at http://localhost:5000/swagger-ui.html

<!-- CONTACT -->
## Contact

Daniel Teixeira dos Santos [![mail][gmail-shield]][gmail-url] [![LinkedIn][linkedin-shield]][linkedin-url]

Project Link: [https://github.com/danieltex/faerun-app](https://github.com/danieltex/faerun-app)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/danieltex/faerun-app.svg?style=for-the-badge
[contributors-url]: https://github.com/danieltex/faerun-app/graphs/contributors
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/danieltex
[gmail-shield]: https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white
[gmail-url]: mailto:danieltex@gmail.com
