import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    java
}

group = "com.jomaleda.ravenpack.interview"
version = "0.0.1-SNAPSHOT"

java {
    // Set the Java version to 21
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Core Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter")
    
    // Spring AOP for method decorators
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Library for parsing and writing CSV files
    implementation("com.opencsv:opencsv:5.9")

    // Lombok for reducing boilerplate code
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Spring Boot Starter for testing (includes JUnit 5, Mockito)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        // Set the JVM target to 21
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}