plugins {
    id("java-library")

    //alias(libs.plugins.paperweight)
    alias(libs.plugins.lombok.plugin)
    alias(libs.plugins.shadow)
}

group = "com.itsschatten"
version = properties.getOrDefault("version", "INVALID VERSION IN PROPERTIES") as String
// paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    //paperweight.paperDevBundle(libs.versions.papermc.ver)
    compileOnly(libs.paper)

    implementation(libs.bundles.common)
    annotationProcessor(libs.lombok)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withJavadocJar() // TODO: This is dumb and it doesn't work, for some reason. test this again please.
    withSourcesJar()
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()

        options.windowTitle = "Yggdrasil"
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