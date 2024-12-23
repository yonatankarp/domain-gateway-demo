plugins {
    id("domain-gateway-demo.java-conventions")
    id("domain-gateway-demo.publishing-conventions")
    alias(libs.plugins.spotless) apply true
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.openapi.generator) apply false
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

allprojects {
    apply(plugin = "com.diffplug.spotless")

    repositories {
        mavenCentral()
    }

    spotless {
        kotlin {
            target(
                fileTree(projectDir) {
                    include("**/*.kt")
                    exclude(
                        "**/.gradle/**",
                        "**/build/generated/**"
                    )
                }
            )
            trimTrailingWhitespace()
            endWithNewline()
            // see https://github.com/shyiko/ktlint#standard-rules
            ktlint("1.5.0")
        }
    }
}
