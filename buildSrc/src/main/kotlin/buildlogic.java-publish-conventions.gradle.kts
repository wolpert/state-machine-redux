plugins {
    // Apply the java Plugin to add support for Java.
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name = "State Machine Redux"
                description = "Core Feature Flag library"
                url = "https://github.com/wolpert/state-machine-redux"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "wolpert"
                        name = "Ned Wolpert"
                        email = "ned.wolpert@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/wolpert/state-machine-redux.git"
                    developerConnection = "scm:git:ssh://github.com/wolpert/state-machine-redux.git"
                    url = "https://github.com/wolpert/state-machine-redux/"
                }
            }

        }
    }
    repositories {
        maven {
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            name = "ossrh"
            credentials(PasswordCredentials::class)
        }
    }
}
signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}
