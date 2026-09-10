plugins {
    id("module.java")
}

dependencies {
    implementation(project(":kernel:kernel-core"))
    implementation(libs.assertj.core)
}