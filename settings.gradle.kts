pluginManagement {
    repositories {
        includeBuild("build-logic")
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
    }
}

rootProject.name = "lauth"

include("kernel:kernel-core")
include("kernel:kernel-test")
include("kernel:kernel-application")
include("kernel:kernel-web")

include("identity-service:application")
include("identity-service:domain")
include("identity-service:launcher")
include("identity-service:jpa")
include("identity-service:fileIO")
include("identity-service:web")
