plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.4"
}

tasks.bootJar { enabled = true }

dependencies {
    implementation(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:3.1.0")
    implementation("software.amazon.awssdk:s3")
    
    // PDF generation (lightweight)
    implementation("org.apache.pdfbox:pdfbox:3.0.4")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:localstack")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}