pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven { url = uri("https://repo.huaweicloud.com/repository/maven/") }
        google()
        mavenCentral()
    }
    versionCatalogs{
        create("androidxLibs") {
            library("asm","org.ow2.asm","asm").version("9.2")
            library("asm-tree","org.ow2.asm","asm-tree").version("9.2")
            library("commons-io","commons-io","commons-io").version("2.6")
            library("kotlinpoet","com.squareup","kotlinpoet").version("1.12.0")
            library("agp","com.android.tools.build","gradle").version("9.0.1")
            bundle("nav-plugin", listOf("asm","asm-tree","commons-io","kotlinpoet","agp"))
        }
    }
}

rootProject.name = "BRedio"
include(":app")
include(":common")
include(":data")
include(":domain")
include(":player")
include(":plugin")
include(":nav-api")
