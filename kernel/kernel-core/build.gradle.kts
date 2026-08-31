plugins {
    id("module.java")
}

group = "com.lilamaris.cozyr"
version = "0.0.1-SNAPSHOT"

dependencies {
    testImplementation(project(":kernel:kernel-test"))
}