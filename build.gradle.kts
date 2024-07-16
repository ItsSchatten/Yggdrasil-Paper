plugins {
    `java-library`
    `maven-publish`

    // Use Mojang mappings and a few other PaperAPI QOL.
    id("io.papermc.paperweight.userdev") version "1.7.1"
    // Automatic lombok and delombok configuration
    id("io.freefair.lombok") version "8.6"

    // Shade libraries into one "UberJar"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

    compileOnly("org.jetbrains:annotations:24.1.0")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

group = "com.itsschatten"
version = "2.0.5"
description = "The library used in most of Paper Plugins coded by ItsSchatten."
java.sourceCompatibility = JavaVersion.VERSION_21


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "$group"
            artifactId = rootProject.name
            version = version

            from(components["java"])
        }
    }
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()

        (options as StandardJavadocDocletOptions).tags(
            listOf(
                "todo:X",
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:"
            )
        )
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}
