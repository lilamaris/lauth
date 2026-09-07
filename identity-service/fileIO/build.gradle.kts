plugins {
    id("module.spring")
    id("autoconfigure.spring")
}

group = "com.lilamaris.lauth"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity-service:application"))

    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))

    implementation(libs.spring.security.jose)
    implementation(platform(libs.bc.jdk18on.bom))
    implementation(libs.bcprov.jdk18on)
    implementation(libs.bcpkix.jdk18on)
}
