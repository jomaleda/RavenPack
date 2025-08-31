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

## Design and Architectural Decisions
1. **Framework**: **Spring Boot** was chosen for its convention-over-configuration approach, which simplifies setup and dependency management for creating standalone applications.
2. **Performance & Scalability**:
    * **Virtual Threads (Project Loom)**: The application uses Java 21's Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`). This allows for massive concurrency, where each message processing task runs on its own lightweight virtual thread. It's the ideal model for I/O-bound workloads like this one, as it eliminates the bottleneck of a limited platform thread pool.
    * **Streaming**: The input file is read as a **Stream**, which processes the file line-by-line instead of loading the entire file into RAM. This ensures a low memory footprint even with millions of records.
    * **Caching**: An in-memory cache (`ConcurrentHashMap`) is implemented for the Translation and Scoring service clients. This adheres to the "idempotent" requirement by drastically reducing network calls for duplicate messages (e.g., spam). In a real scenario, this cache responsibility can be delegated to a cache system like Redis.
3. **CSV Handling**: The OpenCSV library is used for robust and efficient parsing and writing of CSV files.
4. **Testing**: Unit tests are written using JUnit 5 and Mockito to verify the core business logic within the FileProcessorService`, ensuring its correctness.