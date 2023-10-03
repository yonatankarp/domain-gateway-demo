plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.openapi.generator)
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

openApiGenerate {
    generatorName = "kotlin-spring"
    inputSpec = "$projectDir/src/main/resources/api/goodbye-api.yaml"
    outputDir = "${layout.buildDirectory.get()}/generated/openapi/goodbye-service"
    apiPackage = "com.yonatankarp.goodbye.openapi.v1_current"
    modelPackage = "com.yonatankarp.goodbye.openapi.v1_current.models"
    templateDir = "$projectDir/src/main/resources/api/templates/kotlin-spring"
    configOptions = mapOf(
        "dateLibrary" to "java8",
        "interfaceOnly" to "true",
        "implicitHeaders" to "true",
        "hideGenerationTimestamp" to "true",
        "useTags" to "true",
        "documentationProvider" to "none",
        "reactive" to "true",
        "useSpringBoot3" to "true",
    )
}

sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].kotlin {
    srcDir("${layout.buildDirectory.get()}/generated/openapi/goodbye-service/src/main/kotlin")
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

    compileKotlin {
        dependsOn(openApiGenerate)
    }
}

val taskDependencies = mapOf(
    "spotlessKotlin" to listOf("compileKotlin", "processResources"),
)

taskDependencies.forEach {
    val task = it.key
    it.value.forEach { dependsOn ->
        tasks.findByName(task)!!.dependsOn(dependsOn)
    }
}
