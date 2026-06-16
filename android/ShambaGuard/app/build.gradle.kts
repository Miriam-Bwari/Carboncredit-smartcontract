import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.google.services)
}

// Read local secrets — file is gitignored, never committed
fun keysProperty(
    key: String,
    default: String = "",
): String {
    val props = Properties()
    val file = File(rootProject.projectDir, "keys.properties")
    if (file.exists()) FileInputStream(file).use { props.load(it) }
    return props.getProperty(key, default)
}

android {
    namespace = "dev.korryr.shambaguard"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.korryr.shambaguard"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject Maps API key into the manifest
        manifestPlaceholders["MAPS_API_KEY"] = keysProperty("MAPS_API_KEY")

        // Add Base URL for Retrofit
        buildConfigField("String", "BASE_URL", "\"${keysProperty("BASE_URL", "https://api.shambaguard.co.ke/")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navigation 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Logging
    implementation(libs.timber)

    // DataStore — first-launch onboarding flag
    implementation(libs.amdatastore.preferences)

    // Google Maps Compose — farm polygon drawing (Step 2)
    implementation(libs.maps.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Security & Biometric
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.security.crypto)

    // Location & Permissions
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
}

ktlint {
    // Use the latest stable ktlint engine (not the Gradle plugin version)
    version.set("1.5.0")

    // Android mode — enforces Android-specific code style rules
    android.set(true)

    // Show which rule triggered each violation
    verbose.set(true)

    // Stop the build on format issues (use ktlintFormat to auto-fix)
    ignoreFailures.set(false)

    reporters {
        // Prints violations to the terminal
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
    }

    filter {
        // Exclude generated Hilt + KSP files
        exclude("**/generated/**")
        exclude("**/build/**")
        include("**/*.kt")
    }
}
