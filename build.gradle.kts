plugins {
    kotlin("jvm") version "2.0.21"
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("net.researchgate.release") version "3.0.2"
    id("org.openapi.generator") version "7.9.0"
}

group = "com.wesleyhome.aws"
val versionString = providers.gradleProperty("version").get()
version = versionString
description = "Library that simplifies use of AWS Lambda App Config Layer API"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

repositories {
    mavenLocal()
    mavenCentral()
}

openApiGenerate {
    generatorName = "kotlin"
    inputSpec = "$projectDir/model/app-config-api.yml"
    outputDir = "${layout.buildDirectory.get()}/generated"
    apiPackage = "com.wesleyhome.aws.appconfig.api"
    modelPackage = "com.wesleyhome.aws.appconfig.model"
    configOptions.putAll(mapOf(
        "dateLibrary" to "java8",
        "serializationLibrary" to "jackson",
        "library" to "jvm-retrofit2"
    ))
}
sourceSets {
    main {
        kotlin.srcDirs("${layout.buildDirectory.get()}/generated/src/main/kotlin")
    }
}

tasks.compileKotlin {
    dependsOn("openApiGenerate")
}

dependencies {
    val jacksonVersion = "2.18.0"
    val retrofitVersion = "2.11.0"
    val okhttpVersion = "4.12.0"
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation(platform("org.junit:junit-bom:5.11.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))
}
nexusPublishing {
    repositories {
        sonatype()
    }
}

release {
    tagTemplate = "$name-$version"
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

tasks.named("sourcesJar") {
    dependsOn("openApiGenerate")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                description = "Library that makes using AWS AppConfig simpler in AWS Lambda when using AWS AppConfig Layer"
                name = "AWS AppConfig Lambda Layer Helper"
                url = "https://github.com/wesleyhome-com/aws-appconfig-layer-helper"
                inceptionYear = "2024"
                scm {
                    connection = "scm:git:https://github.com/wesleyhome-com/aws-appconfig-layer-helper.git"
                    developerConnection = "scm:git:https://github.com/wesleyhome-com/aws-appconfig-layer-helper.git"
                    url = "https://github.com/wesleyhome-com/aws-appconfig-layer-helper"
                    tag = "HEAD"
                }
                developers {
                    developer {
                        id = "justin"
                        name = "Justin Wesley"
                        roles = listOf("Software Development Engineer")
                    }
                }
                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
            }
        }
    }
}

signing {
    setRequired { !project.version.toString().endsWith("-SNAPSHOT") && !project.hasProperty("skipSigning") }
    if(isOnCIServer()) {
        val signingKey: String? by project
        if((signingKey?.length ?: 0) <= 0){
            throw RuntimeException("No Signing Key")
        }
        useInMemoryPgpKeys(signingKey, "")
    }
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).apply {
            addBooleanOption("html5", true)
        }
    }
}

fun isOnCIServer() = System.getenv("CI") == "true"
