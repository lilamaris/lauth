plugins {
    id("module.java")
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
    testImplementation(libs.spring.boot.starter.flyway)
    testRuntimeOnly(libs.flyway.database.postgresql)
}

tasks.processTestResources {
    from(project(":identity-service:launcher").file("src/main/resources"))
    {
        include("db/migration/**")
    }
}