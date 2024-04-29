import java.net.URL

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()



    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://jcenter.bintray.com") }
        maven { url = uri("https://jitpack.io" )}

    }
}

rootProject.name = "pdf_Manager"
include(":app")
