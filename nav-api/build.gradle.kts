plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
}

group = "com.elyric.nav"
version = "1.0.0-SNAPSHOT"
val artifactId = "nav-api"
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

kotlin {
    jvmToolchain(11)
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group as String?
            artifactId = artifactId
            version = version

            pom {
                name.set("Elyric Navigation Plugin Api")
                description.set("Navigation Annotation Plugin Api")
            }
        }

    }
    repositories {
        mavenLocal()
    }
}
dependencies {
    testImplementation(libs.junit)
}
