plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.sonarqube") version "4.4.1.3373"
    id("jacoco")
}

repositories {
    mavenCentral()
}

group = "fr.slickteam.hubspot.api"
version = "2.2.1-SNAPSHOT"
description = "Java Wrapper for HubSpot API"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

jacoco {
    toolVersion = "0.8.9"
}

dependencies {
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("com.konghq:unirest-java:3.13.11")
    implementation("commons-codec:commons-codec:1.16.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.4.6")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:unchecked")
}

sonar {
    properties {
        property("sonar.projectKey", "Slickteam_hubspot-java")
        property("sonar.organization", "slickteam")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.verbose", "true")
        property("sonar.dynamicAnalysis", "reuseReports")

        val junitReportPath = layout.buildDirectory.dir("test-results/test")
        property("sonar.junit.reportsPaths", junitReportPath.get().asFile)

        val jacocoReportPath = layout.buildDirectory.dir("reports/jacoco.xml")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", jacocoReportPath.get().asFile)
    }
}

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
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassphrase = System.getenv("GPG_SIGNING_PASSPHRASE")
    useInMemoryPgpKeys(signingKey, signingPassphrase)
    val extension = extensions
            .getByName("publishing") as PublishingExtension
    sign(extension.publications)
}