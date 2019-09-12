import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.2.71"
    kotlin("plugin.spring") version "1.2.71"
    id("org.springframework.boot") version "2.1.5.RELEASE"
}

apply(plugin = "io.spring.dependency-management")

version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.getByName<BootJar>("bootJar") {
    launchScript()
    baseName = "citybikes"
}

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    compile("org.telegram:telegrambots-spring-boot-starter:4.3.1")
    compile("org.telegram:telegrambots-abilities:4.3.1")
    compile("org.antlr:ST4:4.1")
    compile("org.springframework.boot:spring-boot-starter:2.1.3.RELEASE")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    compileOnly("org.projectlombok:lombok:1.18.8")
    annotationProcessor("org.projectlombok:lombok:1.18.8")
}
