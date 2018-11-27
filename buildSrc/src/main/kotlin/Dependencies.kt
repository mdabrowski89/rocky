object Versions {

    const val kotlin = "1.3.10"

    const val minSdk = 14
    const val compiledSdk = 28
    const val targetSdk = compiledSdk
    const val buildTools = "28.0.3"

    const val appCompat = "28.0.0-alpha3"
    const val playServiceMaps = "15.0.1"

    const val lifecycle = "1.1.1"

    const val rxKotlin = "2.2.0"
    const val rxBinding = "2.1.1"
    const val rxRelay = "2.0.0"
    const val rxIdler = "0.9.0"

    const val retrofit = "2.4.0"
    const val retrofitLoggingInterceptor = "3.9.1"
    const val retrofitSynchronousAdapter = "0.4.0"

    // test
    const val junit = "4.12"
    const val mockito = "2.21.0"
    const val supportTest = "1.0.2"
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
    const val rxBindingLib = "com.jakewharton.rxbinding2:rxbinding:${Versions.rxBinding}"
    const val rxRelayLib = "com.jakewharton.rxrelay2:rxrelay:${Versions.rxRelay}"
    const val rxIdlerLib = "com.squareup.rx.idler:rx2-idler:${Versions.rxIdler}"

    const val retrofitLib = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGsonConverterLib = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitRxJava2AdapterLib = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofitLoggingInterceptorLib = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofitLoggingInterceptor}"
    const val retrofitSynchronousAdapterLib = "com.jaredsburrows.retrofit:retrofit2-synchronous-adapter:${Versions.retrofitSynchronousAdapter}"

    // test
    const val junitLib = "junit:junit:${Versions.junit}"
    const val mockitoLib = "org.mockito:mockito-core:${Versions.mockito}"
    const val testRunner = "com.android.support.test:runner:${Versions.supportTest}"
    const val testRules = "com.android.support.test:rules:${Versions.supportTest}"
    const val espressoLib = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
}

object Group {
    const val rxJava2 = "io.reactivex.rxjava2"
}

object Module {
    const val rxAndroid = "rxandroid"
    const val rxJava = "rxjava"
}