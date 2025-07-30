
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {

    signingConfigs {
        getByName("debug") {
            storeFile = file("my-release-key-android-driver.jks")
            storePassword = "IT@droppa16"
            keyAlias = "dropper_driver"
            keyPassword = "IT@droppa16"
        }
    }
    val versionProps = Properties()
    val versionPropsFile = file("version.properties")

    if (versionPropsFile.exists()) {
        versionProps.load(FileInputStream(versionPropsFile))
    }

    val code = (versionProps["VERSION_CODE"]?.toString()?.toInt() ?: 0) + 1
    versionProps["VERSION_CODE"] = code.toString()

    namespace = "co.za.kasi"
    compileSdk = 35

    viewBinding {}


    defaultConfig {
        applicationId = "co.za.kasi"
        minSdk = 26
        targetSdk = 35
        versionCode = code
        //TODO(Remember to put the version name in the version.properties file)
        versionName = "1.0.17"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}



dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-messaging:24.1.1")

    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-vision-common:19.1.3")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.9")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.9")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-common:19.1.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")

    implementation("androidx.browser:browser:1.6.0")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.vectordrawable:vectordrawable-seekable:1.0.0-beta01")
    implementation("androidx.legacy:legacy-support-v13:1.0.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.caverock:androidsvg-aar:1.4")


    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation("com.airbnb.android:lottie:3.7.0")

    implementation("com.google.android.material:material:1.12.0-alpha01")

    implementation("androidx.camera:camera-camera2:1.1.0-alpha10")
    implementation("androidx.camera:camera-lifecycle:1.1.0-alpha10")
    implementation("androidx.camera:camera-core:1.1.0-alpha10")

    implementation("com.nineoldandroids:library:2.4.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

//    room dependencies
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    //RxJava
//    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
//    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")


    /*implementation("com.google.android.gms:play-services-places:17.0.0")
    implementation("com.google.android.libraries.places:places:1.1.0")*/

    // Ktor
    //def ktor_version = "1.6.3"
    implementation("io.ktor:ktor-client-core:1.6.3")
    implementation("io.ktor:ktor-client-websockets:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-logging:1.6.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")





}