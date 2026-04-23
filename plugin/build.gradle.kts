plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "com.elyric.plugin"
version = "1.0.1"

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
    implementation("com.elyric.nav:nav-api:1.0.0-SNAPSHOT")
    testImplementation(libs.junit)
}
