plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.protobuf) apply false
}

buildscript {
    val ktlintVersion = "0.49.0"

    configurations.classpath {
        resolutionStrategy {
            force(
                "com.pinterest.ktlint:ktlint-core:$ktlintVersion",
                "com.pinterest.ktlint:ktlint-reporter-checkstyle:$ktlintVersion",
                "com.pinterest.ktlint:ktlint-reporter-json:$ktlintVersion",
                "com.pinterest.ktlint:ktlint-reporter-html:$ktlintVersion",
                "com.pinterest.ktlint:ktlint-reporter-plain:$ktlintVersion",
                "com.pinterest.ktlint:ktlint-reporter-sarif:$ktlintVersion",
                "com.pinterest.ktlint:ktlint-ruleset-standard:$ktlintVersion"
            )
        }
    }
}