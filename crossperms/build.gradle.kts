import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

plugins {
    alias(libs.plugins.unimined)
    alias(libs.plugins.shadow)
}

base {
    archivesName = "crossperms"
}

val api: SourceSet by sourceSets.creating
val common: SourceSet by sourceSets.creating {
//    blossom.javaSources {
//        property("mod_id", modId)
//        property("mod_name", modName)
//        property("version", version.toString())
//        property("license", license)
//        property("author", author)
//        property("description", description)
//        property("homepage_url", homepageUrl)
//    }
}
val bungeecord: SourceSet by sourceSets.creating
val fabric: SourceSet by sourceSets.creating
val fabricLegacy: SourceSet by sourceSets.creating
val forge: SourceSet by sourceSets.creating
val forge1171: SourceSet by sourceSets.creating
val neoforge: SourceSet by sourceSets.creating
val paper: SourceSet by sourceSets.creating
val spigot: SourceSet by sourceSets.creating
val sponge: SourceSet by sourceSets.creating
val velocity: SourceSet by sourceSets.creating
listOf(bungeecord, spigot, velocity).forEach {
    listOf(api, common).forEach { sourceSet ->
        it.compileClasspath += sourceSet.output
        it.runtimeClasspath += sourceSet.output
    }
}

val mainCompileOnly: Configuration by configurations.creating
configurations.compileOnly.get().extendsFrom(mainCompileOnly)
val apiCompileOnly: Configuration by configurations.getting
val commonCompileOnly: Configuration by configurations.getting
val bungeecordCompileOnly: Configuration by configurations.getting
val fabricCompileOnly: Configuration by configurations.getting
val fabricLegacyCompileOnly: Configuration by configurations.getting
val forgeCompileOnly: Configuration by configurations.getting
val forge1171CompileOnly: Configuration by configurations.getting
val neoforgeCompileOnly: Configuration by configurations.getting
val paperCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}
val spigotCompileOnly: Configuration by configurations.getting
val spongeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}
val velocityCompileOnly: Configuration by configurations.getting
listOf(bungeecordCompileOnly, fabricCompileOnly, fabricLegacyCompileOnly, forgeCompileOnly, forge1171CompileOnly,
    neoforgeCompileOnly, paperCompileOnly, spigotCompileOnly, spongeCompileOnly, velocityCompileOnly).forEach {
    it.extendsFrom(apiCompileOnly)
    it.extendsFrom(commonCompileOnly)
}
val modImplementation: Configuration by configurations.creating
val fabricModImplementation: Configuration by configurations.creating {
    extendsFrom(modImplementation)
}

unimined.footgunChecks = false

unimined.minecraft(common) {
    combineWith(sourceSets.main.get())
    if (sourceSet == common) {
        defaultRemapJar = false
    }
    if (sourceSet == common || sourceSet == fabric || sourceSet == forge || sourceSet == forge1171 ||
        sourceSet == neoforge || sourceSet == paper || sourceSet == sponge) {
        version(minecraftVersion)
        mappings {
            parchment(parchmentMinecraft, parchmentVersion)
            mojmap()
            devFallbackNamespace("official")
        }
    }
}

unimined.minecraft(fabric) {
    combineWith(api)
    combineWith(common)
    fabric {
        loader(fabricLoaderVersion)
    }
}

unimined.minecraft(fabricLegacy) {
    combineWith(api)
    combineWith(common)
    version("1.12.2")
    mappings {
        calamus()
        feather(31)
    }
}

unimined.minecraft(forge) {
    combineWith(api)
    combineWith(common)
    minecraftForge {
        loader(forgeVersion)
    }
}

unimined.minecraft(forge1171) {
    combineWith(api)
    combineWith(common)
    version("1.17.1")
    mappings {
        parchment("1.17.1", "2021.12.12")
        mojmap()
        devFallbackNamespace("official")
    }
    minecraftForge {
        loader("37.1.1")
    }
}

unimined.minecraft(neoforge) {
    combineWith(api)
    combineWith(common)
    neoForge {
        loader(neoForgeVersion)
    }
    defaultRemapJar = true
}

unimined.minecraft(paper) {
    combineWith(common)
    combineWith(spigot)
    accessTransformer {
        // https://github.com/PaperMC/Paper/blob/main/build-data/paper.at
        accessTransformer("${rootProject.projectDir}/crossperms/src/paper/paper.at")
    }
    defaultRemapJar = true
}

unimined.minecraft(sponge) {
    combineWith(api)
    combineWith(common)
    defaultRemapJar = true
}

tasks.register<ShadowJar>("relocateFabricJar") {
    dependsOn("remapFabricJar")
    from(jarToFiles("remapFabricJar"))
    archiveClassifier.set("fabric-relocated")
    // relocate("$groupId.$modId.mixin.vanilla", "$groupId.$modId.mixin.y_intmdry")
}

tasks.register<ShadowJar>("relocateForgeJar") {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    archiveClassifier.set("forge-relocated")
    // relocate("$groupId.$modId.mixin.vanilla", "$groupId.$modId.mixin.l_searge")
}

tasks.register<ShadowJar>("relocateForge1171Jar") {
    dependsOn("remapForge1171Jar")
    from(jarToFiles("remapForge1171Jar"))
    archiveClassifier.set("forge1171-relocated")
    // relocate("$groupId.$modId.mixin.vanilla", "$groupId.$modId.mixin.searge")
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.asm.tree)

    // CrossPerms
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.fabricmc:fabric-loader:0.16.9")
    compileOnly("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
    compileOnly("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:1.9.0+1.12.2")
//    compileOnly("org.spongepowered:spongeapi:8.1.0")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    compileOnly("com.mojang:authlib:3.13.56")

    // CrossPerms Integrations
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("ca.stellardrift.permissionsex:permissionsex-core:2.0-SNAPSHOT")
    compileOnly("ca.stellardrift.permissionsex:permissionsex-bukkit:2.0-SNAPSHOT")
    compileOnly("ca.stellardrift.permissionsex:permissionsex-bungee:2.0-SNAPSHOT")
    compileOnly("ca.stellardrift.permissionsex:permissionsex-fabric:2.0-SNAPSHOT")
    compileOnly("ca.stellardrift.permissionsex:permissionsex-sponge:2.0-SNAPSHOT")
    compileOnly("ca.stellardrift.permissionsex:permissionsex-velocity:2.0-SNAPSHOT")

    compileOnly(project(":base"))
    compileOnly(project(":core"))
    compileOnly(project(":metadata"))

    // TODO: Replace with proper source sets
    compileOnly("dev.neuralnexus:entrypoint-spoof:0.1.28")

    apiCompileOnly(libs.jspecify)
    apiCompileOnly("com.mojang:authlib:3.13.56")
    apiCompileOnly(project(":base"))
    apiCompileOnly(project(":core"))
    apiCompileOnly(project(":metadata"))

    commonCompileOnly(libs.mixin)
    commonCompileOnly(project(":base"))
    commonCompileOnly(project(":core"))
    commonCompileOnly(project(":metadata"))
    commonCompileOnly(project(":muxins"))
    forgeCompileOnly(libs.mixin)

    // -----
    mainCompileOnly(libs.jspecify)
    mainCompileOnly(libs.mixin)
    commonCompileOnly(libs.slf4j)
    commonCompileOnly(libs.jspecify)
    commonCompileOnly(files(api.output))
    bungeecordCompileOnly("net.md-5:bungeecord-api:$bungeecordVersion")

    listOf("api-base").forEach {
        fabricModImplementation(fabricApi.fabricModule("fabric-$it", fabricVersion))
    }
    fabricCompileOnly("me.lucko:fabric-permissions-api:0.2-SNAPSHOT") // TODO: JiJ??

    listOf("api-base", "permissions-api-v1").forEach {
        fabricLegacyCompileOnly(fabricApi.legacyFabricModule("legacy-fabric-$it", "1.9.0+1.12.2"))
    }
    fabricLegacyCompileOnly("dev.neuralnexus:entrypoint-spoof:0.1.28") // TODO: Reflect around this

    paperCompileOnly("io.papermc.paper:paper-api:$minecraftVersion-$paperVersion")
    paperCompileOnly(libs.ignite.api)

    spigotCompileOnly("org.spigotmc:spigot-api:$minecraftVersion-$spigotVersion")
    spigotCompileOnly("com.github.MilkBowl:VaultAPI:1.7")

    spongeCompileOnly("org.spongepowered:spongeapi:$spongeVersion")
    velocityCompileOnly("com.velocitypowered:velocity-api:$velocityVersion")
}

tasks.jar {
    dependsOn(
        "relocateFabricJar",
        "relocateForgeJar",
        "relocateForge1171Jar"
    )
    from(
        bungeecord.output,
        jarToFiles("relocateFabricJar"),
        jarToFiles("relocateForgeJar"),
        jarToFiles("relocateForge1171Jar"),
        neoforge.output,
        paper.output,
        sponge.output,
        spigot.output,
        velocity.output
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "TaterLibLite CrossPerms",
                "Specification-Version" to version,
                "Specification-Vendor" to "NeualNexus",
                "Implementation-Version" to version,
                "Implementation-Vendor" to "NeualNexus",
                "Implementation-Timestamp" to Instant.now().toString()
            )
        )
    }
}

tasks.withType<ShadeJar> {
    shadePath = {
        it.substringBefore(".")
            .substringBeforeLast("-")
            .replace(Regex("[.;\\[/]"), "-")
            .replace("metadata", "dev/neuralnexus/taterlib/lite/jvmdg")
    }
}
