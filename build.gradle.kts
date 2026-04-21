import java.time.Instant

plugins {
    java
    `maven-publish`
    alias(libs.plugins.blossom)
    alias(libs.plugins.jvmdowngrader)
    alias(libs.plugins.spotless)
}

base {
    archivesName = modId
}

val mavenCredentials: PasswordCredentials.() -> Unit = {
    username = project.findProperty("neuralNexusUsername") as? String ?: System.getenv("NEURALNEXUS_USERNAME") ?: ""
    password = project.findProperty("neuralNexusPassword") as? String ?: System.getenv("NEURALNEXUS_PASSWORD") ?: ""
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "idea")
    apply(plugin = "eclipse")
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    base {
        version = rootProject.version
    }

    java {
        withSourcesJar()
        withJavadocJar()
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(javaVersion)
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }

    repositories {
        mavenLocal()
        maven("https://maven.neuralnexus.dev/releases")
        maven("https://maven.neuralnexus.dev/snapshots")
        maven("https://maven.neuralnexus.dev/mirror")
    }

    dependencies {
        compileOnly(rootProject.libs.annotations)
        compileOnly(rootProject.libs.jspecify)
    }

    spotless {
        format("misc") {
            target("*.gradle.kts", ".gitattributes", ".gitignore")
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
        }
        java {
            target("src/**/*.java", "src/**/*.java.peb")
            toggleOffOn()
            importOrder()
            removeUnusedImports()
            cleanthat()
            googleJavaFormat("1.35.0")
                .aosp()
                .formatJavadoc(true)
                .reorderImports(true)
            formatAnnotations()
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
            licenseHeader("""/**
 * Copyright (c) 2026 $author
 * This project is Licensed under <a href="$sourceUrl/blob/main/LICENSE">$license</a>
 */""")
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = rootProject.libs.plugins.jvmdowngrader.get().pluginId)

    tasks.downgradeJar {
        downgradeTo = JavaVersion.VERSION_1_8
        archiveClassifier.set("downgraded-8")
    }

    tasks.shadeDowngradedApi {
        downgradeTo = JavaVersion.VERSION_1_8
        archiveClassifier.set("downgraded-8-shaded")
    }

    tasks.downgradeJar.get().dependsOn(tasks.spotlessApply)
    tasks.assemble {
        dependsOn(tasks.downgradeJar)
        dependsOn(tasks.shadeDowngradedApi)
    }

    publishing {
        repositories {
            mavenLocal()
            maven("https://maven.neuralnexus.dev/releases") {
                name = "NeuralNexusReleases"
                credentials(mavenCredentials)
            }
            maven("https://maven.neuralnexus.dev/snapshots") {
                name = "NeuralNexusSnapshots"
                credentials(mavenCredentials)
            }
        }

        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifact(tasks.downgradeJar.get())
                artifact(tasks.shadeDowngradedApi.get())
            }
        }
    }
}

sourceSets.main {
    blossom.javaSources {
        property("mod_id", modId)
        property("mod_name", modName)
        property("version", version.toString())
        property("license", license)
        property("author", author)
        property("description", description ?: "")
        property("homepage_url", homepageUrl)
    }
}

dependencies {
    compileOnly("dev.neuralnexus:entrypoint-spoof:0.1.26")
}

tasks.withType<ProcessResources> {
    filesMatching(listOf(
        "bungee.yml",
        "fabric.mod.json",
        "ignite.mod.json",
        "pack.mcmeta",
        "META-INF/mods.toml",
        "META-INF/neoforge.mods.toml",
        "plugin.yml",
        "paper-plugin.yml",
        "ignite.mod.json",
        "META-INF/sponge_plugins.json",
        "velocity-plugin.json"
    )) {
        expand(project.properties)
    }
}

tasks.jar {
    from(
        project(":base").sourceSets.main.get().output,
        project(":core").sourceSets.main.get().output,
        project(":metadata").sourceSets.main.get().output,
        project(":muxins").sourceSets.main.get().output,
        project(":network").sourceSets.main.get().output
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modName,
                "Specification-Version" to version,
                "Specification-Vendor" to "NeualNexus",
                "Implementation-Version" to version,
                "Implementation-Vendor" to "NeualNexus",
                "Implementation-Timestamp" to Instant.now().toString(),
                "FMLCorePluginContainsFMLMod" to "true",
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "MixinConfigs" to "$modId.mixins.json"
            )
        )
    }
    from(listOf("README.md", "LICENSE")) {
        into("META-INF")
    }
}

tasks.downgradeJar {
    downgradeTo = JavaVersion.VERSION_1_8
    archiveClassifier.set("downgraded-8")
}

tasks.shadeDowngradedApi {
    downgradeTo = JavaVersion.VERSION_1_8
    shadePath = {
        it.substringBefore(".")
            .substringBeforeLast("-")
            .replace(Regex("[.;\\[/]"), "-")
            .replace(modId, "dev/neuralnexus/taterlib/lite/jvmdg")
    }
    archiveClassifier.set("downgraded-8-shaded")
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

tasks.build.get().dependsOn(tasks.spotlessApply)
tasks.downgradeJar.get().dependsOn(tasks.spotlessApply)
tasks.assemble {
    dependsOn(tasks.downgradeJar)
    dependsOn(tasks.shadeDowngradedApi)
}

tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publishes all publications to Maven Local"
    dependsOn(subprojects.map { it.tasks.named("publishAllPublicationsToMavenLocalRepository") })
    dependsOn(tasks.named("publishAllPublicationsToMavenLocalRepository"))
}

tasks.register("publishAllToNeuralNexusSnapshots") {
    group = "publishing"
    description = "Publishes all publications to NeuralNexus Snapshots"
    dependsOn(subprojects.map { it.tasks.named("publishAllPublicationsToNeuralNexusSnapshotsRepository") })
    dependsOn(tasks.named("publishAllPublicationsToNeuralNexusSnapshotsRepository"))
}

tasks.register("publishAllToNeuralNexusReleases") {
    group = "publishing"
    description = "Publishes all publications to NeuralNexus Releases"
    dependsOn(subprojects.map { it.tasks.named("publishAllPublicationsToNeuralNexusReleasesRepository") })
    dependsOn(tasks.named("publishAllPublicationsToNeuralNexusReleasesRepository"))
}

publishing {
    repositories {
        mavenLocal()
        maven("https://maven.neuralnexus.dev/releases") {
            name = "NeuralNexusReleases"
            credentials(mavenCredentials)
        }
        maven("https://maven.neuralnexus.dev/snapshots") {
            name = "NeuralNexusSnapshots"
            credentials(mavenCredentials)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
