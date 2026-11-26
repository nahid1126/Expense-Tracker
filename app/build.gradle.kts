plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.navigation.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.service)
}

val releaseStore = providers.environmentVariable("EXPENSE_RELEASE_STORE_FILE").orNull
val releaseStorePassword = providers.environmentVariable("EXPENSE_RELEASE_STORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("EXPENSE_RELEASE_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("EXPENSE_RELEASE_KEY_PASSWORD").orNull
val hasReleaseSigning = listOf(releaseStore, releaseStorePassword, releaseKeyAlias, releaseKeyPassword)
    .all { !it.isNullOrBlank() }

android {
    namespace = "com.nahid.expensetracker"

    if (hasReleaseSigning) {
        signingConfigs.create("release") {
            storeFile = file(requireNotNull(releaseStore))
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nahid.expensetracker"
        minSdk = 28
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    androidResources {
        localeFilters += "en"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// The signed APK wizard supplies these through Android Studio's Gradle invocation.
val hasIdeSigning = listOf(
    "android.injected.signing.store.file",
    "android.injected.signing.store.password",
    "android.injected.signing.key.alias",
    "android.injected.signing.key.password",
).all { !providers.gradleProperty(it).orNull.isNullOrBlank() }

val verifyReleaseSigning by tasks.registering {
    doLast {
        check(hasReleaseSigning || hasIdeSigning) {
            "Release signing is required. Use Android Studio's Generate Signed Bundle / APK wizard " +
                "or set EXPENSE_RELEASE_STORE_FILE, EXPENSE_RELEASE_STORE_PASSWORD, " +
                "EXPENSE_RELEASE_KEY_ALIAS and EXPENSE_RELEASE_KEY_PASSWORD."
        }
    }
}

tasks.matching { it.name in setOf("packageRelease", "packageReleaseBundle") }.configureEach {
    dependsOn(verifyReleaseSigning)
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val buildType = variant.name
            val name = android.defaultConfig.versionName
            val code = android.defaultConfig.versionCode
            output.outputFileName.set("ExpenseTracker-${name}-${code}-$buildType.apk")
        }
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.constraint.layout)
    implementation(libs.material)
    implementation(libs.androidx.exifinterface)
    implementation (libs.accompanist.systemuicontroller)


    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
    implementation(libs.lottie)
    implementation(libs.lottie.compose)
    implementation(libs.material.icon)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.dataStore)
    implementation(libs.kotlinx.datetime)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.work.runtime.ktx)
}