plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.sonarqube") version "6.0.1.5171"
    id("jacoco")
    id("org.jreleaser") version "1.17.0"
}

repositories {
    mavenCentral()
}

group = "fr.slickteam.hubspot.api"
version = "2.2.3"
description = "Java Wrapper for HubSpot API"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

jacoco {
    toolVersion = "0.8.12"
}

dependencies {
    implementation("com.google.guava:guava:33.4.5-jre")
    implementation("com.konghq:unirest-java:3.14.5")
    implementation("commons-codec:commons-codec:1.18.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.4.6")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:unchecked")
}

tasks.jar{
    enabled = true
    // Remove `plain` postfix from jar file name
    archiveClassifier.set("")
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
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

jreleaser {
    gitRootSearch = true

    project {
        description = "Java Wrapper for HubSpot API"
        license = "GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007"
    }

    signing {
        setActive("ALWAYS")
        armored = true

    }
    deploy {
        setActive("ALWAYS")
        maven {
            setActive("ALWAYS")
            nexus2 {
                create("maven-central") {
                    setActive("ALWAYS")
                    url.set("https://s01.oss.sonatype.org/service/local")
                    closeRepository.set(false)
                    releaseRepository.set(false)
                    stagingRepositories.add("build/staging-deploy")
                }
            }
        }
    }

    release {
        github {
            skipRelease = true
            skipTag = true
            overwrite = false
            token = "none"
        }
    }
}