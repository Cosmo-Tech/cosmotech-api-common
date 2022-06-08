// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
plugins {
    val kotlinVersion = "1.6.0"
    kotlin("jvm") version kotlinVersion

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

val kotlinJvmTarget = 17

java { toolchain { languageVersion.set(JavaLanguageVersion.of(kotlinJvmTarget)) } }

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

version = "0.0.1"

tasks.jar {
    manifest {
        attributes(mapOf("Implementation-Title" to project.name,
            "Implementation-Version" to project.version))
    }
}

// Dependencies version
//Implementation
val swaggerParserVersion = "2.0.31"
val hashidsVersion = "1.0.3"
val springOauthAutoConfigureVersion = "2.6.6"
val springSecurityJwtVersion = "1.1.1.RELEASE"

//compileOnly
val springBootStarterWebVersion = "2.7.0"
val springDocVersion = "1.6.6"
val springOauthVersion = "1.6.6"
val zalandoSpringProblemVersion = "0.27.0"
val servletApiVersion = "4.0.1"
val oktaSpringBootVersion = "2.1.5"

// Tests
val jUnitBomVersion="5.8.2"
val mockkVersion="1.12.4"
val awaitilityKVersion="4.2.0"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("org.hashids:hashids:${hashidsVersion}")

    implementation("io.swagger.parser.v3:swagger-parser-v3:${swaggerParserVersion}")

    implementation(
        "org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:${springOauthAutoConfigureVersion}")
    implementation("org.springframework.security:spring-security-jwt:${springSecurityJwtVersion}")

    compileOnly("org.springframework.boot:spring-boot-starter-web:${springBootStarterWebVersion}") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }

    compileOnly("org.springdoc:springdoc-openapi-ui:${springDocVersion}")
    compileOnly("org.springdoc:springdoc-openapi-kotlin:${springDocVersion}")
    compileOnly("org.zalando:problem-spring-web-starter:${zalandoSpringProblemVersion}")
    compileOnly("javax.servlet:javax.servlet-api:${servletApiVersion}")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.springframework.security:spring-security-oauth2-jose:${springOauthVersion}")
    compileOnly("org.springframework.security:spring-security-oauth2-resource-server:${springOauthVersion}")
    compileOnly("com.okta.spring:okta-spring-boot-starter:${oktaSpringBootVersion}")

    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:${jUnitBomVersion}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("org.awaitility:awaitility-kotlin:${awaitilityKVersion}")
}
