plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.ok2c.hc.android"

    compileSdk = 33

    defaultConfig {
        minSdk = 19
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/ASL2.0")
    }
}

dependencies {
    val versions: Map<String, String> by project.extra

    api("org.apache.httpcomponents.client5:httpclient5:${versions["httpclient"]}")
    testImplementation("org.hamcrest:hamcrest-library:${versions["hamcrest"]}")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
}

tasks.register<Javadoc>("androidJavadocs") {
    val androidLibrary = project.the(com.android.build.gradle.LibraryExtension::class)

    source(androidLibrary.sourceSets["main"].java.srcDirs)
    classpath += project.files(androidLibrary.bootClasspath.joinToString(File.pathSeparator))
    androidLibrary.libraryVariants.find { it.name == "release" }?.apply {
        classpath += javaCompileProvider.get().classpath
    }

    exclude("**/R.html", "**/R.*.html", "**/index.html")

    val stdOptions = options as StandardJavadocDocletOptions
    stdOptions.addBooleanOption("Xdoclint:-missing", true)
    stdOptions.links(
            "http://docs.oracle.com/javase/7/docs/api/",
            "http://developer.android.com/reference/",
            "https://hc.apache.org/httpcomponents-core-5.1.x/current/httpcore5/apidocs/",
            "https://hc.apache.org/httpcomponents-core-5.1.x/current/httpcore5-h2/apidocs/",
            "https://hc.apache.org/httpcomponents-client-5.1.x/current/httpclient5/apidocs/")
}

tasks.register<Jar>("androidJavadocsJar") {
    val javadocTask = tasks.getByName<Javadoc>("androidJavadocs")
    dependsOn(javadocTask)
    archiveClassifier.set("javadoc")
    from(javadocTask.destinationDir)
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

afterEvaluate {
    val releaseVersion: Boolean by project.extra

    apply<MavenPublishPlugin>()

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenArtifacts") {
                from(components["release"])
                artifact(tasks.getByName<Jar>("androidJavadocsJar"))
                artifact(tasks.getByName<Jar>("androidSourcesJar"))

                pom {
                    name.set("Apache HttpClient extensions for Android")
                    description.set("Apache HttpClient extensions for Android")
                    url.set("https://github.com/ok2c/httpclient-android-ext")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("ok2c")
                            name.set("Oleg Kalnichevski")
                            email.set("olegk@apache.org")
                        }
                    }

                    scm {
                        connection.set("scm:git:git@github.com:ok2c/httpclient-android-ext.git")
                        developerConnection.set("scm:git:ssh://github.com:ok2c/httpclient-android-ext.git")
                        url.set("https://github.com/ok2c/httpclient-android-ext")
                    }
                }
            }
        }
        if (releaseVersion) {
            apply<SigningPlugin>()

            val publishing = project.the(PublishingExtension::class)
            configure<SigningExtension> {
                useGpgCmd()
                sign(publishing.publications["mavenArtifacts"])
            }
        }
    }
}
