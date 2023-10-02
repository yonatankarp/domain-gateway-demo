plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.kotlin.all)
    implementation(libs.bundles.springboot.all)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    jar {
        enabled = false
    }

    check {
        finalizedBy(spotlessApply)
    }

    test {
        useJUnitPlatform()
    }
}

val taskDependencies = mapOf(
    "spotlessKotlin" to listOf("compileKotlin")
)

taskDependencies.forEach {
    val task = it.key
    it.value.forEach { dependsOn ->
        tasks.findByName(task)!!.dependsOn(dependsOn)
    }
}
