plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt.gradle)
    id("maven-publish")
    kotlin("kapt")
}

android {
    namespace = "com.jibase"
    compileSdk = 34

    defaultConfig {
        minSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.bundles.dataStore)
    implementation(libs.bundles.coroutine)
    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.bundles.hilt)
    kapt(libs.bundles.hilt.compiler)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "ngocji"
            artifactId = "jicore"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}