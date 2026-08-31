import extension.libs
import extension.requireLibrary

plugins {
    id("platform.spring")
}

dependencies {
    runtimeOnly(libs.requireLibrary("h2database"))
}
