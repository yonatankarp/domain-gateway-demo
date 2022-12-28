plugins {
    id("jacoco")
    id("pmd")
    id("domain-gateway-demo.java-conventions")
    id("domain-gateway-demo.code-metrics")
    id("domain-gateway-demo.publishing-conventions")
    id("com.diffplug.spotless") version "6.12.0" apply true
    id("org.springframework.boot") version "2.7.6" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    kotlin("jvm") version "1.7.22" apply false
    kotlin("plugin.spring") version "1.8.0" apply false
    id("org.openapi.generator") version "6.2.1" apply false
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
