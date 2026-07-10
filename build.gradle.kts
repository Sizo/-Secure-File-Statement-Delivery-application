plugins {
    java
    id("org.springframework.boot") version "3.4.13" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.sonarqube") version "5.0.0.4638"
    id("com.github.spotbugs") version "6.0.12" apply false
    id("org.openapi.generator") version "7.4.0" apply false
    jacoco
}

val awsSdkVersion: String by project
val springCloudAwsVersion: String by project
val jacocoToolVersion: String = project.property("jacocoVersion") as String

allprojects {
    group = project.property("group").toString()
    version = project.property("version").toString()

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    apply(plugin = "com.github.spotbugs")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    configure<CheckstyleExtension> {
        toolVersion = "10.15.0"
        isIgnoreFailures = false
    }

    configure<PmdExtension> {
        toolVersion = "7.0.0"
        isIgnoreFailures = false
        ruleSets = listOf("category/java/errorprone.xml", "category/java/bestpractices.xml")
    }

    configure<com.github.spotbugs.snom.SpotBugsExtension> {
        ignoreFailures.set(false)
    }

    tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
        reports {
            create("html") {
                required.set(true)
            }
        }
    }

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.13")
            mavenBom("software.amazon.awssdk:bom:${awsSdkVersion}")
            mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:${springCloudAwsVersion}")
        }
    }

    dependencies {
        // Lombok
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")

        // Testing
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    jacoco {
        toolVersion = jacocoToolVersion
    }

    val jacocoExclusions = listOf(
        "**/config/**",
        "**/dto/**",
        "**/entity/**",
        "**/exception/**",
        "**/*Application*"
    )

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    exclude(jacocoExclusions)
                }
            })
        )
    }

    tasks.jacocoTestCoverageVerification {
        dependsOn(tasks.jacocoTestReport)
        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    exclude(jacocoExclusions)
                }
            })
        )
        violationRules {
            rule {
                element = "BUNDLE"
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }

    tasks.named("check") {
        dependsOn(tasks.jacocoTestCoverageVerification)
    }
}
