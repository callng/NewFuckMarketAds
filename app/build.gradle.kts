plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.owo233.fuckmarketads"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.owo233.fuckmarketads"
        minSdk = 31
        targetSdk = 36
        versionCode = 26
        versionName = "1.2.6"
        buildConfigField("String", "APP_NAME", "\"Fuck Market Ads\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        packaging {
            resources {
                excludes += "**"
                merges += "META-INF/xposed/*"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    compileOnly(libs.libxposed.api)
    implementation(libs.ezxhelper.core)
}
