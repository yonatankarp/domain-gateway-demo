import org.openapitools.generator.gradle.plugin.tasks.GenerateTask as GenerateOpenApiTask

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.openapi.generator)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvm.get()))
    }
}

dependencies {
    implementation(libs.bundles.kotlin.all)
    implementation(libs.bundles.springboot.all)

    api(libs.bundles.retrofit.all)
    compileOnly(libs.retrofit2.scalars)

    testImplementation(libs.bundles.tests.all)
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
    "spotlessKotlin" to listOf("compileKotlin", "compileTestKotlin", "test", "processResources"),
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
val openApiGenerateOutputDir = "${layout.buildDirectory.get()}/generated/openapi"

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
            "reactive" to "true",
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
    }
}

/********************************************/
/******* OPEN API SPEC GENERATION END *******/
/********************************************/
