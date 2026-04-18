plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "com.elyric.plugin"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

gradlePlugin {
    plugins {
        create("navPlugin") {
            id = "com.elyric.nav-plugin"
            implementationClass = "com.elyric.plugin.nav_plugin.NavPlugin"
            displayName = "Elyric Navigation Plugin"
            description = "Custom Gradle plugin for BRedio navigation-related build logic."
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(androidxLibs.bundles.nav.plugin)
    testImplementation(libs.junit)
}
