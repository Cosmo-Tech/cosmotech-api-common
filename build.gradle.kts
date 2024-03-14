// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.implementation

plugins {
  val kotlinVersion = "1.9.10"
  kotlin("jvm") version kotlinVersion
  id("com.diffplug.spotless") version "6.22.0"
  id("org.springframework.boot") version "3.2.2" apply false
  id("io.gitlab.arturbosch.detekt") version "1.23.1"
  id("pl.allegro.tech.build.axion-release") version "1.15.5"
  id("org.jetbrains.kotlinx.kover") version "0.7.4"
  `maven-publish`
  // Apply the java-library plugin for API and implementation separation.
  `java-library`
}

scmVersion {
  ignoreUncommittedChanges.set(false)
  useHighestVersion.set(true)
  tag { prefix.set("") }
}

project.version = scmVersion.version

val kotlinJvmTarget = 17

java { toolchain { languageVersion.set(JavaLanguageVersion.of(kotlinJvmTarget)) } }

publishing {
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/Cosmo-Tech/cosmotech-api-common")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
      }
    }
  }

  publications {
    create<MavenPublication>("maven") {
      groupId = "com.github.Cosmo-Tech"
      artifactId = "cosmotech-api-common"
      version = scmVersion.version
      pom {
        name.set("Cosmo Tech API common")
        description.set("Cosmo Tech API common library for Platform")
        url.set("https://github.com/Cosmo-Tech/cosmotech-api-common")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://github.com/Cosmo-Tech/cosmotech-api-common/blob/main/LICENSE")
          }
        }
      }

      from(components["java"])
    }
  }
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

configure<SpotlessExtension> {
  isEnforceCheck = false

  val licenseHeaderComment =
      """
        // Copyright (c) Cosmo Tech.
        // Licensed under the MIT license.
      """
          .trimIndent()

  java {
    googleJavaFormat()
    target("**/*.java")
    licenseHeader(licenseHeaderComment)
  }
  kotlin {
    ktfmt("0.41")
    target("**/*.kt")
    licenseHeader(licenseHeaderComment)
  }
  kotlinGradle {
    ktfmt("0.41")
    target("**/*.kts")
    //      licenseHeader(licenseHeaderComment, "import")
  }
}

tasks.withType<JavaCompile>() { options.compilerArgs.add("-parameters") }

tasks.withType<Detekt>().configureEach {
  buildUponDefaultConfig = true // preconfigure defaults
  allRules = false // activate all available (even unstable) rules.
  config.from(file("$rootDir/.detekt/detekt.yaml"))
  jvmTarget = kotlinJvmTarget.toString()
  ignoreFailures = project.findProperty("detekt.ignoreFailures")?.toString()?.toBoolean() ?: false
  // Specify the base path for file paths in the formatted reports.
  // If not set, all file paths reported will be absolute file path.
  // This is so we can easily map results onto their source files in tools like GitHub Code
  // Scanning
  basePath = rootDir.absolutePath
  reports {
    html {
      // observe findings in your browser with structure and code snippets
      required.set(true)
      outputLocation.set(
          file("${layout.buildDirectory.get()}/reports/detekt/${project.name}-detekt.html"))
    }
    xml {
      // checkstyle like format mainly for integrations like Jenkins
      required.set(false)
      outputLocation.set(
          file("${layout.buildDirectory.get()}/reports/detekt/${project.name}-detekt.xml"))
    }
    txt {
      // similar to the console output, contains issue signature to manually edit baseline files
      required.set(true)
      outputLocation.set(
          file("${layout.buildDirectory.get()}/reports/detekt/${project.name}-detekt.txt"))
    }
    sarif {
      // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations
      // with Github Code Scanning
      required.set(true)
      outputLocation.set(
          file("${layout.buildDirectory.get()}/reports/detekt/${project.name}-detekt.sarif"))
    }
  }
}

tasks.jar {
  manifest {
    attributes(
        mapOf("Implementation-Title" to project.name, "Implementation-Version" to project.version))
  }
}

// Dependencies version

// Required versions
val jacksonVersion = "2.17.0"
val springWebVersion = "6.1.4"
val springBootVersion = "3.2.2"

// Implementation
val swaggerParserVersion = "2.1.16"
val hashidsVersion = "1.0.3"
val springOauthAutoConfigureVersion = "2.6.8"
val springSecurityJwtVersion = "1.1.1.RELEASE"
val springDocVersion = "2.2.0"
val springOauthVersion = "6.2.2"
val servletApiVersion = "6.0.0"
val oktaSpringBootVersion = "3.0.5"
val tikaVersion = "2.9.1"
val kubernetesClientVersion = "19.0.0"
val redisOMVersion = "0.8.8"
val kotlinCoroutinesCoreVersion = "1.7.3"

// Checks
val detektVersion = "1.23.1"

// Tests
val jUnitBomVersion = "5.10.0"
val mockkVersion = "1.13.8"
val awaitilityKVersion = "4.2.0"
val testcontainersRedis = "1.6.4"

dependencies {
  implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

  detekt("io.gitlab.arturbosch.detekt:detekt-cli:$detektVersion")
  detekt("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:$detektVersion")

  // Align versions of all Kotlin components
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  // Use the Kotlin JDK 8 standard library.
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("org.hashids:hashids:${hashidsVersion}")

  implementation("io.swagger.parser.v3:swagger-parser-v3:${swaggerParserVersion}")

  implementation(
      "org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:${springOauthAutoConfigureVersion}") {
        constraints {
          implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
          implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
          implementation("org.springframework:spring-web:$springWebVersion")
          implementation("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
        }
      }
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.security:spring-security-oauth2-jose:${springOauthVersion}")
  implementation(
      "org.springframework.security:spring-security-oauth2-resource-server:${springOauthVersion}")
  implementation("org.springframework.security:spring-security-jwt:${springSecurityJwtVersion}")

  implementation("org.springframework.boot:spring-boot-starter-web") {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
  }
  implementation("io.kubernetes:client-java:${kubernetesClientVersion}")

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")
  // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0-rc1")

  implementation("jakarta.servlet:jakarta.servlet-api:${servletApiVersion}")
  implementation("com.okta.spring:okta-spring-boot-starter:${oktaSpringBootVersion}")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("io.micrometer:micrometer-registry-prometheus")
  implementation("org.springframework.boot:spring-boot-starter-aop")

  implementation("org.apache.tika:tika-core:${tikaVersion}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesCoreVersion")
  implementation("com.redis.om:redis-om-spring:${redisOMVersion}")

  implementation("com.redis.testcontainers:testcontainers-redis-junit:$testcontainersRedis")
  implementation("org.springframework.boot:spring-boot-starter-test")

  testImplementation(kotlin("test"))
  testImplementation(platform("org.junit:junit-bom:${jUnitBomVersion}"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("io.mockk:mockk:${mockkVersion}")
  testImplementation("org.awaitility:awaitility-kotlin:${awaitilityKVersion}")

  // Use the Kotlin test library.
  testImplementation("org.jetbrains.kotlin:kotlin-test")

  // Use the Kotlin JUnit integration.
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

extensions.configure<kotlinx.kover.gradle.plugin.dsl.KoverReportExtension> {
  defaults {
    // reports configs for XML, HTML, verify reports
  }
  filters {
    includes {
      packages("com.cosmotech.api")
      classes("com.cosmotech.api.id.*")
      classes("com.cosmotech.api.rbac.*")
      classes("com.cosmotech.utils.*")
    }
  }
}
