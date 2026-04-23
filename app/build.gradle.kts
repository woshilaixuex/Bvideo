plugins {
    alias(libs.plugins.android.application)
    id("com.elyric.nav-plugin") version "1.0.1"
}

android {
    namespace = "com.elyric.bredio"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.elyric.bredio"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // 添加对其他模块的依赖
    implementation(project(":common"))
    implementation(project(":player"))
    implementation(project(":domain"))
    implementation(project(":data"))
    // 媒体
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.androidx.media3.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(project(":nav-api"))
}
