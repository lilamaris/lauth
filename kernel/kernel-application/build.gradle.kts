plugins {
    id("module.java")
    `java-library`
}

group = "com.lilamaris.cozyr"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":kernel:kernel-core"))

    testImplementation(project(":kernel:kernel-test"))
}