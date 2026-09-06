plugins {
    id("module.spring")
    id("autoconfigure.spring")
}

dependencies {
    implementation(project(":identity-service:domain"))

    implementation(project(":kernel:kernel-application"))
    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))

    implementation(libs.spring.security.crypto)
    implementation(libs.spring.security.jose)
    implementation(libs.bcprov.jdk18on)
}