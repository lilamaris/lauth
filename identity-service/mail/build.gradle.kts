plugins {
    id("module.java")
    id("module.spring")
    id("autoconfigure.spring")
}

dependencies {
    implementation(project(":identity-service:application"))

    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))
}
