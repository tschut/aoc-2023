kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(21))
    }
}

plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "nl.tiemenschut"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.github.tschut:aoc-dsl:0.4.0")
}
