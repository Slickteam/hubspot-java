plugins {
    `java-library`
    `maven-publish`
}

repositories {
    jcenter()
    mavenCentral()
}

group = "fr.slickteam.hubspotApi"
version = "1.3.4-SNAPSHOT"
description = "Java Wrapper for HubSpot API"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.json:json:20220320")
    implementation("com.mashape.unirest:unirest-java:1.4.9")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("org.codehaus.jackson:jackson-mapper-asl:1.9.13")
    implementation("org.mockito:mockito-core:3.4.6")
    testImplementation ("junit:junit:4.13.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:unchecked")
}


val nexusLogin: String? by project
val nexusPassword: String?  by project

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials{
                username = nexusLogin
                password = nexusPassword
            }
        }
    }
}