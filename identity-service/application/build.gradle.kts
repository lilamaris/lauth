plugins {
    id("module.spring")
    id("autoconfigure.spring")
    `java-library`
}

dependencies {
    implementation(project(":identity-service:domain"))

    api(project(":kernel:kernel-application"))
    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))

    api(libs.spring.security.jose)
    implementation(libs.spring.security.crypto)

    implementation(platform(libs.bc.jdk18on.bom))
    implementation(libs.bcprov.jdk18on)
    implementation(libs.bcpkix.jdk18on)
}