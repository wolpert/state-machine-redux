
plugins {
    id("buildlogic.java-library-conventions")
    id("buildlogic.java-publish-conventions")
}

dependencies {

    implementation(project(":smr"))
    implementation(libs.codehead.metrics)
    implementation(libs.slf4j.api)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.codehead.metrics.test)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.logback)

}
