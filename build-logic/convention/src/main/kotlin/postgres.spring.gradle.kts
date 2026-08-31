import extension.libs
import extension.requireLibrary

plugins {
    id("platform.spring")
}

dependencies {
    runtimeOnly(libs.requireLibrary("postgresql"))

    testImplementation(libs.requireLibrary("spring-boot-testcontainers"))
    testImplementation(libs.requireLibrary("testcontainers-postgresql"))
}
