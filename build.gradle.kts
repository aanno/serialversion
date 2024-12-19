plugins {
    id("java")
}

group = "com.github.aanno.serialversion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    val artifactory = System.getenv("ARTIFACTORY_URL")
    if (artifactory != null) {
        maven {
            url = uri(artifactory)
            credentials {
                username = System.getenv("ARTIFACTORY_CREDENTIALS_USR")
                password = System.getenv("ARTIFACTORY_CREDENTIALS_PSW")
            }
        }
    }
}

val downloadJar1 by configurations.creating
val downloadJar2 by configurations.creating

val classgraph: String by project
val guava: String by project
val junit: String by project
val jakarta_annotations: String by project

dependencies {
    implementation("io.github.classgraph:classgraph:$classgraph")
    implementation("com.google.guava:guava:$guava")

    testImplementation(platform("org.junit:junit-bom:$junit"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    downloadJar1("jakarta.annotation:jakarta.annotation-api:$jakarta_annotations")
    // new configuration did not obey to bom stuff (aanno)
    downloadJar1("org.junit.jupiter:junit-jupiter:$junit")

    downloadJar2("org.junit.jupiter:junit-jupiter:$junit")
}

tasks {
    named<Wrapper>("wrapper") {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "8.11.1"
    }

    named("build") {
        dependsOn("testJars1", "testJars2")
    }

    test {
        useJUnitPlatform()
    }

    register<Copy>("testJars1") {
        from(downloadJar1)
        into("build/test-jars-1")
    }

    register<Copy>("testJars2") {
        from(downloadJar2)
        into("build/test-jars-2")
    }
}
