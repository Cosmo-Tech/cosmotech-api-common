// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.kotlin.dsl.implementation

plugins {
  val kotlinVersion = "2.0.21"
  kotlin("jvm") version kotlinVersion
  id("com.diffplug.spotless") version "7.0.3"
  id("org.springframework.boot") version "3.4.4" apply false
  id("io.gitlab.arturbosch.detekt") version "1.23.8"
  id("pl.allegro.tech.build.axion-release") version "1.18.18"
  id("org.jetbrains.kotlinx.kover") version "0.9.1"
  id("project-report")
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

val kotlinJvmTarget = 21

java {
  targetCompatibility = JavaVersion.VERSION_21
  sourceCompatibility = JavaVersion.VERSION_21
  toolchain { languageVersion.set(JavaLanguageVersion.of(kotlinJvmTarget)) }
}

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
    ktfmt()
    target("**/*.kt")
    licenseHeader(licenseHeaderComment)
  }
  kotlinGradle {
    ktfmt()
    target("**/*.kts")
    licenseHeader(licenseHeaderComment, "(import |// no-import)")
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

tasks.test { useJUnitPlatform() }

// Dependencies version

// Required versions
val jacksonVersion = "2.18.3"
val springWebVersion = "6.2.1"
val springBootVersion = "3.4.4"

// Implementation
val swaggerParserVersion = "2.1.25"
val hashidsVersion = "1.0.3"
val springOauthAutoConfigureVersion = "2.6.8"
val springSecurityJwtVersion = "1.1.1.RELEASE"
val springDocVersion = "2.8.6"
val springOauthVersion = "6.4.4"
val servletApiVersion = "6.1.0"
val oktaSpringBootVersion = "3.0.7"
val tikaVersion = "3.1.0"
val redisOMVersion = "0.9.10"
val kotlinCoroutinesCoreVersion = "1.10.2"

// Checks
val detektVersion = "1.23.8"

// Tests
val jUnitBomVersion = "5.12.2"
val mockkVersion = "1.14.4"
val awaitilityKVersion = "4.3.0"
val testcontainersRedis = "1.6.4"

dependencies {
  // https://youtrack.jetbrains.com/issue/KT-71057/POM-file-unusable-after-upgrading-to-2.0.20-from-2.0.10
  implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.0.21"))
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

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

  implementation("jakarta.servlet:jakarta.servlet-api:${servletApiVersion}")
  implementation("com.okta.spring:okta-spring-boot-starter:${oktaSpringBootVersion}")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("io.micrometer:micrometer-registry-prometheus")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.apache.httpcomponents.client5:httpclient5")

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
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

extensions.configure<KoverProjectExtension>("kover") {
  reports {
    filters {
      includes {
        packages("com.cosmotech.api")
        classes("com.cosmotech.api.id.*")
        classes("com.cosmotech.api.rbac.*")
        classes("com.cosmotech.utils.*")
      }
    }
  }
}

kover {
  reports {
    total {
      // reports configs for XML, HTML, verify reports
    }
  }
}
