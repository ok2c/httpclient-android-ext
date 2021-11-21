buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.2")
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}

val versions = mapOf(
        "httpclient" to "5.1.2",
        "junit" to "4.13.1",
        "hamcrest" to "2.2"
)

allprojects {
    group = "com.github.ok2c.hc5.android"
    version = "0.1.2-SNAPSHOT"

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

                    if (project.extra.has("ossrh.user")) {
                        credentials {
                            username = project.extra["ossrh.user"] as String
                            password = project.extra["ossrh.password"] as String
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
