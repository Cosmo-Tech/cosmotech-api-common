// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.*
import com.github.jk1.license.task.CheckLicenseTask
import com.github.jk1.license.task.ReportTask
import io.gitlab.arturbosch.detekt.Detekt
import java.io.FileOutputStream
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  val kotlinVersion = "1.8.0"
  kotlin("jvm") version kotlinVersion
  id("com.diffplug.spotless") version "6.12.0"
  id("org.springframework.boot") version "2.7.11" apply false
  id("io.gitlab.arturbosch.detekt") version "1.22.0"
  id("pl.allegro.tech.build.axion-release") version "1.14.3"
  id("org.jetbrains.kotlinx.kover") version "0.6.1"
  id("com.github.jk1.dependency-license-report") version "1.17"
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

var licenseReportDir = "$projectDir/doc/licenses"

val configBuildDir = "$buildDir/config"

mkdir(configBuildDir)

fun downloadLicenseConfigFile(name: String): String {
  val localPath = "$configBuildDir/$name"
  val f = file(localPath)
  f.delete()
  val url = "https://raw.githubusercontent.com/Cosmo-Tech/cosmotech-license/main/config/$name"
  logger.info("Downloading license config file from $url to $localPath")
  uri(url).toURL().openStream().use { it.copyTo(FileOutputStream(f)) }
  return localPath
}

val licenseNormalizerPath = downloadLicenseConfigFile("license-normalizer-bundle.json")
val licenseAllowedPath =
    if (project.properties["useLocalLicenseAllowedFile"] == "true") {
      "$projectDir/config/allowed-licenses.json"
    } else {
      downloadLicenseConfigFile("allowed-licenses.json")
    }

logger.info("Using licenses allowed file: $licenseAllowedPath")

val licenseEmptyPath = downloadLicenseConfigFile("empty-dependencies-resume.json")
// Plugin uses a generated report to check the licenses in a prepation task
val hardCodedLicensesReportPath = "project-licenses-for-check-license-task.json"

licenseReport {
  outputDir = licenseReportDir
  allowedLicensesFile = file(licenseAllowedPath)
  renderers =
      arrayOf<ReportRenderer>(
          InventoryHtmlReportRenderer("index.html"),
          JsonReportRenderer("project-licenses-for-check-license-task.json", false))
  filters = arrayOf<LicenseBundleNormalizer>(LicenseBundleNormalizer(licenseNormalizerPath, true))
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
      outputLocation.set(file("$buildDir/reports/detekt/${project.name}-detekt.html"))
    }
    xml {
      // checkstyle like format mainly for integrations like Jenkins
      required.set(false)
      outputLocation.set(file("$buildDir/reports/detekt/${project.name}-detekt.xml"))
    }
    txt {
      // similar to the console output, contains issue signature to manually edit baseline files
      required.set(true)
      outputLocation.set(file("$buildDir/reports/detekt/${project.name}-detekt.txt"))
    }
    sarif {
      // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations
      // with Github Code Scanning
      required.set(true)
      outputLocation.set(file("$buildDir/reports/detekt/${project.name}-detekt.sarif"))
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
// Implementation
val swaggerParserVersion = "2.1.13"
val hashidsVersion = "1.0.3"
val springOauthAutoConfigureVersion = "2.6.8"
val springSecurityJwtVersion = "1.1.1.RELEASE"
val springBootStarterWebVersion = "2.7.0"
val springDocVersion = "1.6.13"
val springOauthVersion = "5.8.3"
val zalandoSpringProblemVersion = "0.27.0"
val servletApiVersion = "4.0.1"
val oktaSpringBootVersion = "2.1.6"
val azureSpringBootBomVersion = "3.14.0"
val tikaVersion = "2.6.0"
val kubernetesClientVersion = "18.0.0"
val jedisVersion = "3.9.0"
val jredistimeseriesVersion = "1.6.0"
val redisOMVersion = "0.6.4"

// Tests
val jUnitBomVersion = "5.9.1"
val mockkVersion = "1.13.2"
val awaitilityKVersion = "4.2.0"
val testcontainersRedis = "1.6.2"

dependencies {
  implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

  detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.22.0")
  detekt("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")

  // Align versions of all Kotlin components
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  // Use the Kotlin JDK 8 standard library.
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("org.hashids:hashids:${hashidsVersion}")

  implementation("io.swagger.parser.v3:swagger-parser-v3:${swaggerParserVersion}")

  implementation(
      "org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:${springOauthAutoConfigureVersion}")
  implementation("org.springframework.security:spring-security-jwt:${springSecurityJwtVersion}")

  implementation("org.springframework.boot:spring-boot-starter-web") {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
  }
  implementation("io.kubernetes:client-java:${kubernetesClientVersion}")

  implementation("org.springdoc:springdoc-openapi-ui:${springDocVersion}")
  implementation("org.springdoc:springdoc-openapi-kotlin:${springDocVersion}")
  implementation("org.zalando:problem-spring-web-starter:${zalandoSpringProblemVersion}")
  implementation("javax.servlet:javax.servlet-api:${servletApiVersion}")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.security:spring-security-oauth2-jose:${springOauthVersion}")
  implementation(
      "org.springframework.security:spring-security-oauth2-resource-server:${springOauthVersion}")
  implementation("com.okta.spring:okta-spring-boot-starter:${oktaSpringBootVersion}")

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("io.micrometer:micrometer-registry-prometheus")
  implementation("org.springframework.boot:spring-boot-starter-aop")

  implementation("org.apache.tika:tika-core:${tikaVersion}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

  implementation("redis.clients:jedis:${jedisVersion}")
  implementation("com.redislabs:jredistimeseries:${jredistimeseriesVersion}")
  implementation("com.redis.om:redis-om-spring:${redisOMVersion}")
  implementation("com.redislabs:jredisgraph:2.5.1") {
    constraints { implementation("org.apache.commons:commons-text:1.10.0") }
  }
  implementation("com.redis.testcontainers:testcontainers-redis-junit:$testcontainersRedis")
  implementation("org.springframework.boot:spring-boot-starter-test")
  implementation("org.apache.httpcomponents:httpclient:4.5.14")

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

kover {
  filters {
    classes {
      includes +=
          listOf("com.cosmotech.api.id.*", "com.cosmotech.api.rbac.*", "com.cosmotech.utils.*")
    }
  }
}

// https://github.com/jk1/Gradle-License-Report/blob/master/README.md
tasks.register<ReportTask>("generateLicenseDoc") {}

tasks.register<CheckLicenseTask>("validateLicense") {
  dependsOn("generateLicenseDoc")
  // Gradle task must be rerun each time to take new allowed-license into account.
  // Due to an issue in the plugin, we must define each module name for null licenses
  // to avoid false negatives in the allowed-license file.
  outputs.upToDateWhen { false }
}

tasks.withType<KotlinCompile> {
  // Run licensing tasks before compiling
  if (project.properties["skipLicenses"] != "true") {
    dependsOn("validateLicense")
  }
}

tasks.register("displayLicensesNotAllowed") {
  val notAllowedFile =
      file(
          buildString {
            append(licenseReportDir)
            append("/dependencies-without-allowed-license.json")
          })
  val dependenciesEmptyResumeTemplate = file(licenseEmptyPath)
  if (notAllowedFile.exists() &&
      (notAllowedFile.readText() != dependenciesEmptyResumeTemplate.readText())) {
    logger.warn("Licenses not allowed:")
    logger.warn(notAllowedFile.readText())
    logger.warn(
        "Please review licenses and add new license check rules in https://github.com/Cosmo-Tech/cosmotech-license")
  }
}

gradle.buildFinished {
  if (project.properties["skipLicenses"] != "true") {
    val displayTask = tasks.getByName("displayLicensesNotAllowed")
    displayTask.run {}
  }
}
