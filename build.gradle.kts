plugins {
    `java-library`
    `maven-publish`
}

group = "com.github.lukesky19"
version = "1.7.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")

    // SkyLib
    compileOnly("com.github.lukesky19:SkyLib:2.0.2.0")

    // Integration
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.lukesky19:SkyShop:3.3.1.0")
    compileOnly("world.bentobox:bentobox:2.7.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.17-SNAPSHOT") {
        exclude(group = "com.google.code.gson")
        exclude(group = "com.google.guava")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        archiveClassifier.set("")
    }

    build {
        dependsOn(publishToMavenLocal)
        dependsOn(javadoc)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}