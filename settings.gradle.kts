pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/")}
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/")}
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "Med"
include(":app")
include(":ui_atoms")
include(":feature_auth")
include(":core_api")
include(":core")
include(":main")
include(":core_impl")
include(":feature_auth_api")
include(":ui_kit")
include(":feature_home")
include(":feature_home_api")
include(":main_api")
include(":base_arch")
include(":feature_calendar")
include(":feature_calendar_api")
