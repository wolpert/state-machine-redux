
plugins {
    id("buildlogic.java-library-conventions")
    id("buildlogic.java-publish-conventions")
}

dependencies {

    implementation(project(":smr"))
    implementation(libs.bundles.jackson)
    implementation(libs.slf4j.api)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.logback)
}
