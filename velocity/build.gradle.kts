plugins {
    id("java-library")

    alias(libs.plugins.lombok.plugin)
    alias(libs.plugins.shadow)
}

group = "com.itsschatten"
version = properties.getOrDefault("velocity-version", "INVALID VERSION IN PROPERTIES") as String

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    compileOnly(libs.velocity)

    implementation(libs.commons.lang)

    implementation(libs.bundles.common)
    annotationProcessor(libs.lombok)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withJavadocJar()
    withSourcesJar()
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()

        options.windowTitle = "Yggdrasil Velocity"
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