plugins {
    id("module.java")
    `java-library`
}

dependencies {
    api(project(":kernel:kernel-core"))

    testImplementation(project(":kernel:kernel-test"))
}