plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "com.elyric.plugin"
version = "1.0.3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

gradlePlugin {
    plugins {
        create("navPlugin") {
            id = "com.elyric.nav-plugin"
            implementationClass = "com.elyric.plugin.nav_plugin.runtime.NavPlugin"
            displayName = "Elyric Navigation Plugin"
            description = "Custom Gradle plugin for BRedio navigation-related build logic."
            tags.set(listOf("navigation", "plugin","gradle9"))
        }
    }
}

publishing {
    publications {
        withType<MavenPublication>().configureEach {
            pom {
                name.set("Elyric Navigation Plugin")
                description.set("Custom Gradle plugin for BRedio navigation-related build logic.")
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation(gradleApi())
    implementation(androidxLibs.bundles.nav.plugin)
    implementation(project(":nav-api"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.2.10-2.0.2")
    testImplementation(libs.junit)
}
