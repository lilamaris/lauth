plugins {
    id("module.java")
    id("platform.spring")
    `java-library`
}

dependencies {
    compileOnlyApi(libs.jakarta.persistence.api)

    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))
}