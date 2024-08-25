
plugins {
    id("buildlogic.java-library-conventions")
    id("buildlogic.java-publish-conventions")
}

dependencies {

    implementation(libs.jsr305)
    implementation(libs.slf4j.api)
    implementation(libs.immutables.annotations)
    annotationProcessor(libs.immutables.value)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.logback)

}
