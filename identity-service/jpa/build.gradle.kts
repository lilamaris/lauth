plugins {
    id("module.spring")
    id("postgres.spring")
    id("h2.spring")
}

dependencies {
    implementation(project(":identity-service:application"))
    implementation(project(":identity-service:domain"))

    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))

    implementation(libs.spring.boot.starter.data.jpa)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
}
