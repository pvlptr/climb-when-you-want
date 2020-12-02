/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.7.1/userguide/building_java_projects.html
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use JCenter for resolving dependencies.
    jcenter()
}

dependencies {

    implementation("org.jsoup:jsoup:1.13.1")
//    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
//    implementation("com.amazonaws:aws-lambda-java-events:3.1.0")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.0.1")
    //runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:2.13.0")
//    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.2.0")

    implementation("org.apache.logging.log4j:log4j-api:2.14.0")
    implementation("org.apache.logging.log4j:log4j-core:2.14.0")



    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:29.0-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("climb.App")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "climb.App"
    }
}

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
}
