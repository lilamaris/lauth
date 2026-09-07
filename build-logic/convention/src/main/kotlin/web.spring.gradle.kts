import extension.libs
import extension.requireLibrary

plugins {
    id("platform.spring")
}

dependencies {
    implementation(libs.requireLibrary("spring-boot-starter-webmvc"))
    implementation(libs.requireLibrary("springdoc-openapi-starter-webmvc-ui"))

    testImplementation(libs.requireLibrary("spring-boot-starter-webmvc-test"))
}