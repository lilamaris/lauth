import extension.libs
import extension.requireLibrary

plugins {
    id("platform.spring")
}

dependencies {
    implementation(libs.requireLibrary("jakarta-servlet-api"))
    implementation(libs.requireLibrary("spring-boot-starter-security"))
    testImplementation(libs.requireLibrary("spring-security-test"))
}