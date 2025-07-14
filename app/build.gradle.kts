plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "vn.fpt.qcmb_mobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.fpt.qcmb_mobile"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation(libs.gson)
    implementation(libs.swiperefreshlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")


    // WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.4")

    // Circle ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // SharedPreferences
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Swipe Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
}