plugins {
    id("autoconfigure.spring")
    id("module.java")
    id("web.spring")
}

group = "com.lilamaris.lauth"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity-service:application"))

    implementation(project(":kernel:kernel-web"))
    testImplementation(project(":kernel:kernel-test"))
}