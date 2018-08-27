object Versions {

    const val kotlin = "1.2.61"

    const val minSdk = 14
    const val compiledSdk = 28
    const val targetSdk = compiledSdk

    const val appCompat = "28.0.0-alpha1"
    const val playServiceMaps = "15.0.1"

    const val lifecycle = "1.1.1"

    const val rxKotlin = "2.2.0"
    const val rxBinding = "2.1.1"
    const val rxRelay = "2.0.0"

    // test
    const val junit = "4.12"
    const val testRunner = "1.0.2"
    const val espresso = "3.0.2"
}

object Dependencies {

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    const val appcompatLib = "com.android.support:appcompat-v7:${Versions.appCompat}"
    const val cardViewLib = "com.android.support:cardview-v7:${Versions.appCompat}"
    const val playServiceMapsLib = "com.google.android.gms:play-services-maps:${Versions.playServiceMaps}"

    const val lifecycleExtensionsLib = "android.arch.lifecycle:extensions:${Versions.lifecycle}"
    const val lifecycleViewModelsLib = "android.arch.lifecycle:viewmodel:${Versions.lifecycle}"

    const val rxKotlinLib = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxBinding = "com.jakewharton.rxbinding2:rxbinding:${Versions.rxBinding}"
    const val rxRelay = "com.jakewharton.rxrelay2:rxrelay:${Versions.rxRelay}"

    // test
    const val junitLib = "junit:junit:${Versions.junit}"
    const val testRunner = "com.android.support.test:runner:${Versions.testRunner}"
    const val espressoLib = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
}