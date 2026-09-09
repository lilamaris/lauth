import extension.libs
import extension.requireLibrary

plugins {
    id("platform.spring")
}

dependencies {
    implementation(libs.requireLibrary("spring-boot-starter-data-redis"))
    testImplementation(libs.requireLibrary("spring-boot-starter-data-redis-test"))
}
