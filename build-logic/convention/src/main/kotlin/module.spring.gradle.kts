import extension.libs
import extension.requireLibrary

plugins {
    id("module.java")
    id("platform.spring")
}

dependencies {
    implementation(libs.requireLibrary("spring-tx"))
    implementation(libs.requireLibrary("spring-context"))
}
