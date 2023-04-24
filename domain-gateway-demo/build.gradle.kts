import org.openapitools.generator.gradle.plugin.tasks.GenerateTask as GenerateOpenApiTask

plugins {
    id("jacoco")
    id("domain-gateway-demo.code-metrics")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.openapi.generator")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val coroutinesVersion = "1.6.4"
val mockkVersion = "1.13.2"
val mockkSpringVersion = "3.1.1"
val okHttpVersion = "4.11.0"
val retrofitVersion = "2.9.0"

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
    api("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    getByName<Jar>("jar") {
        enabled = false
    }

    build {
        finalizedBy(spotlessApply)
    }

    withType<Test> {
        useJUnitPlatform()
        finalizedBy(spotlessApply)
        finalizedBy(jacocoTestReport)
        finalizedBy(pmdTest)
    }

    jacoco {
        toolVersion = "0.8.7"
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

val taskDependencies = mapOf(
    "spotlessKotlin" to listOf("compileKotlin", "compileTestKotlin", "test", "jacocoTestReport"),
)

taskDependencies.forEach {
    val task = it.key
    it.value.forEach { dependsOn ->
        tasks.findByName(task)!!.dependsOn(dependsOn)
    }
}

/********************************************/
/********* OPEN API SPEC GENERATION *********/
/********************************************/

val apiDirectoryPath = "$projectDir/src/main/resources/api"
val openApiGenerateOutputDir = "$buildDir/generated/openapi"

/**
 * A class represents a specific spec to generate.
 */
data class ApiSpec(
    val name: String,
    val taskName: String,
    val directoryPath: String,
    val outputDir: String,
    val specFileName: String,
    val generatorType: String,
    val packageName: String,
    val modelPackageName: String,
    val config: Map<String, String>,
    val templateDir: String? = null
)

/**
 * List of all api specs to generate
 */
val supportedApis = listOf(
    ApiSpec(
        name = "Gateway API",
        taskName = "generateGatewayApi",
        directoryPath = apiDirectoryPath,
        templateDir = "$apiDirectoryPath/templates/kotlin-spring",
        outputDir = "$openApiGenerateOutputDir/domain-gateway",
        specFileName = "gateway-api.yaml",
        generatorType = "kotlin-spring",
        packageName = "com.yonatankarp.gateway.openapi.v1",
        modelPackageName = "com.yonatankarp.gateway.openapi.v1.models",
        config = mapOf(
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "implicitHeaders" to "true",
            "hideGenerationTimestamp" to "true",
            "useTags" to "true",
            "documentationProvider" to "none",
            "useSpringBoot3" to "true",
        )
    ),
    ApiSpec(
        name = "Hello API",
        taskName = "generateHelloApi",
        directoryPath = apiDirectoryPath,
        outputDir = "$openApiGenerateOutputDir/domain-gateway",
        specFileName = "hello-api.yaml",
        generatorType = "kotlin",
        packageName = "com.yonatankarp.hello.openapi.v1_current",
        modelPackageName = "com.yonatankarp.hello.openapi.v1_current.models",
        config = mapOf(
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "implicitHeaders" to "true",
            "hideGenerationTimestamp" to "true",
            "useTags" to "true",
            "documentationProvider" to "none",
            "serializationLibrary" to "jackson",
            "useCoroutines" to "true",
            "library" to "jvm-retrofit2"
        )
    ),
    ApiSpec(
        name = "Goodbye API",
        taskName = "generateGoodbyeApi",
        directoryPath = apiDirectoryPath,
        outputDir = "$openApiGenerateOutputDir/domain-gateway",
        specFileName = "goodbye-api.yaml",
        generatorType = "kotlin",
        packageName = "com.yonatankarp.goodbye.openapi.v1_current",
        modelPackageName = "com.yonatankarp.goodbye.openapi.v1_current.models",
        config = mapOf(
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "implicitHeaders" to "true",
            "hideGenerationTimestamp" to "true",
            "useTags" to "true",
            "documentationProvider" to "none",
            "serializationLibrary" to "jackson",
            "useCoroutines" to "true",
            "library" to "jvm-retrofit2"
        )
    )
)

// Iterate over the api list and register them as generator tasks
supportedApis.forEach { api ->
    tasks.create(api.taskName, GenerateOpenApiTask::class) {
        group = "openapi tools"
        description = "Generate the code for ${api.name}"

        generatorName.set(api.generatorType)
        inputSpec.set("${api.directoryPath}/${api.specFileName}")
        outputDir.set(api.outputDir)
        apiPackage.set(api.packageName)
        modelPackage.set(api.modelPackageName)
        configOptions.set(api.config)
        api.templateDir?.let { this.templateDir.set(it) }
    }
}

tasks {
    register("cleanGeneratedCodeTask") {
        description = "Removes generated Open API code"
        group = "openapi tools"

        doLast {
            logger.info("Cleaning up generated code")
            File(openApiGenerateOutputDir).deleteRecursively()
        }
    }

    clean {
        dependsOn("cleanGeneratedCodeTask")
        supportedApis.forEach { finalizedBy(it.taskName) }
    }

    compileKotlin {
        supportedApis.forEach { dependsOn(it.taskName) }
    }
}

supportedApis.first().let {
    sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].java {
        srcDir("${it.outputDir}/src/main/kotlin")
        exclude(
            "**/ApiClient.kt",
            "**/OAuth.kt",
            "**/OAuthOkHttpClient.kt"
        )
    }
}

/********************************************/
/******* OPEN API SPEC GENERATION END *******/
/********************************************/
