plugins {
    id("module.java")
    id("platform.spring")
    `java-library`
}

dependencies {
    api(project(":kernel:kernel-application"))

    testImplementation(project(":kernel:kernel-test"))

    api(libs.spring.web)
    api(libs.jakarta.servlet.api)
    api(libs.tools.jackson.databind)
}