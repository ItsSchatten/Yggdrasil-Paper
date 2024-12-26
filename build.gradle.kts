plugins {
    `maven-publish`

    // Uses Mojang mappings and a few other Paper QOL.
    alias(libs.plugins.paperweight) apply false
    // Automatic lombok and delombok configuration.
    alias(libs.plugins.lombok.plugin) apply false
    // Shade libraries into a single "Uberjar".
    alias(libs.plugins.shadow) apply false
}

group = "com.itsschatten"
version = properties.getOrDefault("version", "INVALID VERSION IN PROPERTIES") as String
description = "The library used in most of Paper Plugins coded by ItsSchatten."

subprojects {
    plugins.apply("maven-publish")

    // This 'if-else' statement is required to properly build the Bill of Materials.
    if (!project.name.contains("bom", true)) {
        plugins.apply("java-library")

        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])
                    group = "$group"
                    artifactId = rootProject.name + (
                            if (project.name.equals("common", true))
                                ""
                            else
                                "-" + project.name
                            )
                }
            }
        }
    } else {
        plugins.apply("java-platform")

        publishing {
            publications {
                create<MavenPublication>("bom") {
                    from(components["javaPlatform"])
                    group = "$group"
                    artifactId = rootProject.name + "-bom"
                }
            }
        }
    }
}
