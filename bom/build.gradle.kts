plugins {
    id("java-platform")
    id("maven-publish")
}

group = "com.itsschatten"
version = "1.0.2"

dependencies {
    constraints {
        api(project(":anvilgui"))
        api(project(":common"))
        api(project(":menus"))
        api(project(":velocity"))
        api(project(":wands"))
    }
}