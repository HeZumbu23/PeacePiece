import java.util.Base64

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.hezumbu23.peacepiece"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.hezumbu23.peacepiece"
        minSdk = 26
        targetSdk = 34
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("VERSION_NAME") ?: "1.0.0"
    }

    signingConfigs {
        create("release") {
            val signingKeyB64 = System.getenv("SIGNING_KEY")?.takeIf { it.isNotBlank() }
            if (signingKeyB64 != null) {
                val tempKeystore = File.createTempFile("signing", ".jks")
                tempKeystore.writeBytes(Base64.getDecoder().decode(signingKeyB64))
                storeFile = tempKeystore
                storePassword = System.getenv("KEY_STORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            } else {
                storeFile = rootProject.file("keystore/debug.jks")
                storePassword = "peacepiece"
                keyAlias = "peacepiece"
                keyPassword = "peacepiece"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}
