import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    alias(libs.plugins.detekt)
}

val localProperties = Properties()
try {
    project.rootProject.file("local.properties").reader().use { localProperties.load(it) }
} catch (e: Exception) {
    println("Warning: Could not load local.properties file. ${e.message}")
}
val apiToken = localProperties.getProperty("API_TOKEN") ?: ""
val useHardcodedIdOverride =
    localProperties.getProperty("USE_HARDCODED_ACCOUNT_ID_OVERRIDE", "false")

android {
    namespace = "shmr.budgetly"
    compileSdk = 36

    defaultConfig {
        applicationId = "shmr.budgetly"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_TOKEN", "\"$apiToken\"")
    }

    buildTypes {
        debug {
            buildConfigField("Boolean", "USE_HARDCODED_ACCOUNT_ID", useHardcodedIdOverride)
        }
        release {
            isMinifyEnabled = false
            buildConfigField("Boolean", "USE_HARDCODED_ACCOUNT_ID", "false")
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

    kotlinOptions {
        jvmTarget = "11"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    autoCorrect = true

    parallel = true

    source.setFrom(
        files(
            "src/main/java",
            "src/test/java",
            "src/androidTest/java"
        )
    )
}

dependencies {
    // Core & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.material)

    // Dependency Injection (Dagger 2)
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Network (Retrofit + OkHttp + Kotlinx Serialization)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.material)
}