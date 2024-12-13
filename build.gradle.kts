plugins {
    java
}

group = "com.github.lukesky19"
version = "1.2.0"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }

    maven("https://jitpack.io") {
        name = "jitpack"
    }

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://maven.enginehub.org/repo/")

    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.lukesky19:SkyLib:1.1.0")
    compileOnly("com.github.lukesky19:SkyShop:2.0.0-Pre-Release-2")

    // Hooks
    compileOnly("dev.rosewood:rosestacker:1.5.30")
    compileOnly("world.bentobox:bentobox:2.6.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.11")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }

    archiveClassifier.set("")
}
