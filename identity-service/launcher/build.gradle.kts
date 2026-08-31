plugins {
    id("module.spring")
    id("observability.spring")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":identity-service:application"))
    implementation(project(":identity-service:domain"))
    implementation(project(":identity-service:jpa"))

    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.flyway.database.postgresql)
}