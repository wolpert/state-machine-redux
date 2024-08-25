plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}
nexusPublishing {
    repositories {
        sonatype()
    }
}

// gradle clean build test publishToSonatype closeAndReleaseSonatypeStagingRepository