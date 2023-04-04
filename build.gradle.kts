plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

group = "fr.slickteam.hubspot.api"
version = "1.4.0-SNAPSHOT"
description = "Java Wrapper for HubSpot API"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.konghq:unirest-java:3.13.11")
    implementation("org.mockito:mockito-core:3.4.6")
    testImplementation ("junit:junit:4.13.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:unchecked")
}

val ossrhLogin: String? by project
val ossrhPassword: String? by project

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            withBuildIdentifier()
            pom {
                name.set("hubspot-java")
                description.set("Java Wrapper for HubSpot API")
                url.set("https://github.com/Slickteam/hubspot-java")
                licenses {
                    license {
                        name.set("GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("DepositFix")
                        name.set("DepositFix")
                    }
                    developer {
                        id.set("Slickteam")
                        name.set("Slickteam")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/Slickteam/hubspot-java.git")
                    developerConnection.set("scm:git:git@github.com:Slickteam/hubspot-java.git")
                    url.set("https://github.com/Slickteam/hubspot-java")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = ossrhLogin
                password = ossrhPassword
            }

        }
    }
}

signing {
    useGpgCmd()
    sign(configurations.archives.get())
}
