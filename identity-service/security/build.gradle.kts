plugins {
    id("module.java")
    id("security.spring")
    id("autoconfigure.spring")
}

group = "com.lilamaris.lauth"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity-service:application"))

    implementation(project(":kernel:kernel-web"))
    testImplementation(project(":kernel:kernel-test"))

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.tools.jackson.databind)
}