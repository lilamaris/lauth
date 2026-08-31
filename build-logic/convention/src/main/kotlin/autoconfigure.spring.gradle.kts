import extension.libs
import extension.requireLibrary

plugins {
    id("platform.spring")
}

dependencies {
    implementation(libs.requireLibrary("spring-boot-starter-validation"))
    implementation(libs.requireLibrary("spring-boot-autoconfigure"))
    annotationProcessor(libs.requireLibrary("spring-boot-configuration-processor"))
}
