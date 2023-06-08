plugins {
    id("jacoco")
    id("pmd")
    id("domain-gateway-demo.java-conventions")
    id("domain-gateway-demo.code-metrics")
    id("domain-gateway-demo.publishing-conventions")
    id("com.diffplug.spotless") version "6.19.0" apply true
    id("org.springframework.boot") version "3.1.0" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    val kotlinVersion = "1.8.22"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    id("org.openapi.generator") version "6.6.0" apply false
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
        maven {
            url = uri("https://maven.pkg.github.com/yonatankarp/domain-gateway-demo")
            credentials {
                username = findProperty("gpr.user")?.toString() ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("gpr.key")?.toString() ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
