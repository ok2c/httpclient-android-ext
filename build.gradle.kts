buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}

val versions = mapOf(
        "httpclient" to "5.0-beta4",
        "junit" to "4.12",
        "hamcrest" to "1.3"
)

allprojects {
    group = "com.github.ok2c.hc5.android"
    version = "0.1.1-SNAPSHOT"

    repositories {
        mavenLocal()
        google()
        jcenter()
    }

    extra["releaseVersion"] = !version.toString().endsWith("-SNAPSHOT")
    extra["versions"] = versions
}

subprojects {

    afterEvaluate {
        val releaseVersion: Boolean by project.extra

        apply<MavenPublishPlugin>()

        configure<PublishingExtension> {
            repositories {
                maven {
                    val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
                    url = uri(if (releaseVersion) releasesRepoUrl else snapshotsRepoUrl)

                    val userName = project.extra["ossrh.user"] as String?
                    val userPassword = project.extra["ossrh.password"] as String?

                    if (userName != null) {
                        credentials {
                            username = userName
                            password = userPassword
                        }
                    }
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
