object Libs {

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val supportv4 = "androidx.legacy:legacy-support-v4:1.0.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.3.0"
        const val cardview = "androidx.cardview:cardview:1.0.0"
        const val supportannotations = "androidx.annotation:annotation:1.0.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val corektx = "androidx.core:core-ktx:1.10.1"
    }

    object GooglePlay {
        private const val  billingVersion = "6.0.0"
        const val playCore  = "com.google.android.play:core:1.10.3"
        const val billingClient = "com.android.billingclient:billing:${billingVersion}"
    }

    object Material {
        const val design  = "com.google.android.material:material:1.9.0"
    }

    object Test {
        private const val junitVersion = "4.13.2"
        private const val mockitoCoreVersion = "2.19.0"

        const val junit ="junit:junit:${junitVersion}"
        const val mockitoCore = "org.mockito:mockito-core:${mockitoCoreVersion}"
        const val runner = "androidx.test:runner:1.5.2"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
        const val extJunit = "androidx.test.ext:junit:1.1.5"
    }
}
