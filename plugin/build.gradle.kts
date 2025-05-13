plugins {
    java

    // Automatic lombok and delombok configuration
    alias(libs.plugins.lombok.plugin)

    // Shade libraries into one "UberJar"
    alias(libs.plugins.shadow)
}

group = "com.itsschatten"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly(libs.paper)

    implementation(project(":anvilgui"))
    implementation(project(":common"))
    implementation(project(":menus"))
    implementation(project(":wands"))
}


val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(targetJavaVersion)
}

tasks.withType<ProcessResources>().configureEach {
    filteringCharset = "UTF-8"

    val map = mapOf("version" to version as String)

    filesMatching("*plugin.yml") {
        expand(map)
    }
}

tasks.shadowJar.configure {
    destinationDirectory.set(file(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "dev-servers" + File.separator + "TestPlugin Server (Manual)" + File.separator + "plugins"))
}

