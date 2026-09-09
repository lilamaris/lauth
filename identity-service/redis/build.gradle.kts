plugins {
    id("module.java")
    id("redis.spring")
}

dependencies {
    implementation(project(":identity-service:application"))

    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))

    implementation(libs.tools.jackson.databind)
}