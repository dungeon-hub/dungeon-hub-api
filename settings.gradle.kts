pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven("https://snapshots-repo.kordex.dev")
        maven("https://releases-repo.kordex.dev")
    }
}

rootProject.name = "dungeon-hub-api"

include(":model")
include(":client")
