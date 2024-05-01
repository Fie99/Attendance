import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.attfirebase"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.attfirebase"
        minSdk = 30
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding= true

    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth")

    //Lottie Animation
    implementation ("com.airbnb.android:lottie:6.1.0")


    //ZXing library to scan/generate QR code
    implementation ("com.google.zxing:core:3.4.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
  //  implementation ("androimads.library.qrgenearrator:QRGenearator:1.8.8")
  // implementation ("me.dm7.barcodescanner:zxing:1.9.13")
   // implementation("com.budiyev.android:code-scanner:2.1.8")


    // FirebaseUI for Firebase Realtime Database
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")

    // FirebaseUI for Cloud Firestore
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")

    // FirebaseUI for Firebase Auth
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")

    // FirebaseUI for Cloud Storage
    implementation ("com.firebaseui:firebase-ui-storage:8.0.2")

    implementation ("com.android.support:multidex:1.0.3")

    //implementation (fileTree("dir: 'libs', include: ['*.jar']"))


    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")

    // CameraX core library
    implementation ("androidx.camera:camera-core:1.3.1")

// CameraX Camera2 extension library
    implementation ("androidx.camera:camera-camera2:1.3.1")

// CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:1.3.1")

// CameraX View library (optional, if you want to use a preview view)
    implementation ("androidx.camera:camera-view:1.3.1")

    implementation ("androidx.core:core:1.12.0")

}