# Content Moderation System

This is a Java application built with Spring Boot that processes a CSV file of user messages, scores them for offensive content using simulated external services, and generates a summary report per user.

The project is built using **Gradle with the Kotlin DSL** and leverages modern Java features for high performance.

## Prerequisites

* **Java 21 or higher**. Amazon Corretto 21 is recommended.

## How to Build the Project

This project uses the Gradle Wrapper, which means you don't need to install Gradle on your system.

**Building the Application:**
Navigate to the project's root directory and run the following command to compile the project and package it into an executable JAR file.

On macOS/Linux:

```bash
./gradlew build
```
On Windows:

```bash
gradlew.bat build
```
This will generate an executable JAR file in the build/libs/ directory.

## How to Run the Application
To run the application, you must provide the path to the input CSV file and the path where the output CSV file should be saved.

**All input and output files should be stored on the `data` folder.** A `SecurityException` will be thrown if a file path does not start on `data` folder.

```bash
java -jar build/libs/content-moderation-system-0.0.1-SNAPSHOT.jar <path-to-input.csv> <path-to-output.csv>
```

### Example:
```bash
java -jar build/libs/content-moderation-system-0.0.1-SNAPSHOT.jar data/input.csv data/output.csv
```

### File Formats
* `input.csv`: Must contain the columns `user_id` and `message`.
* `output.csv`: Will be generated with the columns `user_id`, `total_messages`, and `avg_score`.
