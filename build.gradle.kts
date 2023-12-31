plugins {
    kotlin("jvm") version "1.9.21"
}

group = "com.cucumbersaurus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation("com.soywiz.korlibs.korio:korio-jvm:4.0.10")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.jar{
    val path = System.getenv("path")
    logger.info(path)
    destinationDirectory = File(path)
}