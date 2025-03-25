// versions
val minecraftVersion = "1.21.1"
val minecraftDep = "=1.21.1"
// https://parchmentmc.org/docs/getting-started
val parchmentVersion = "2024.11.17"
// https://fabricmc.net/develop
val loaderVersion = "0.16.9"
val fapiVersion = "0.114.0+1.21.1"

// buildscript
plugins {
    id("fabric-loom") version "1.10.+"
    id("maven-publish")
}

base.archivesName = "yeet"
group = "io.github.tropheusj"

val buildNum = providers.environmentVariable("GITHUB_RUN_NUMBER")
    .filter(String::isNotEmpty)
    .map { "build.$it" }
    .orElse("local")
    .get()

version = "1.1.1+$buildNum-mc$minecraftVersion"

repositories {
    maven("https://maven.parchmentmc.org")
}

dependencies {
    // dev environment
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings { nameSyntheticMembers = false }
        parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // dependencies
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fapiVersion")
}

tasks.withType(ProcessResources::class) {
    val properties: Map<String, Any> = mapOf(
        "version" to version,
        "loader_version" to loaderVersion,
        "fapi_version" to fapiVersion,
        "minecraft_dependency" to minecraftDep
    )

    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}
