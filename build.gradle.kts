import java.time.Instant

plugins {
    id("java")
    id("maven-publish")
    id("idea")
    id("eclipse")
    alias(libs.plugins.blossom)
    alias(libs.plugins.spotless)
}

base {
    archivesName = modName
}

val mavenCredentials: PasswordCredentials.() -> Unit = {
    username = project.findProperty("neuralNexusUsername") as? String ?: System.getenv("NEURALNEXUS_USERNAME") ?: ""
    password = project.findProperty("neuralNexusPassword") as? String ?: System.getenv("NEURALNEXUS_PASSWORD") ?: ""
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "idea")
    apply(plugin = "eclipse")
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    // base {}

    java {
        withSourcesJar()
        withJavadocJar()
        toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
        //sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        maven("https://maven.neuralnexus.dev/mirror")
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
            googleJavaFormat("1.24.0")
                .aosp()
                .formatJavadoc(true)
                .reorderImports(true)
            formatAnnotations()
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
            licenseHeader("""/**
 * Copyright (c) 2025 $author
 * This project is Licensed under <a href="$sourceUrl/blob/main/LICENSE">$license</a>
 */""")
        }
    }

    publishing.repositories {
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
}

sourceSets.main {
    blossom.javaSources {
        property("mod_id", modId)
        property("mod_name", modName)
        property("version", version.toString())
        property("license", license)
        property("author", author)
        property("description", description)
        property("homepage_url", homepageUrl)
    }
}

repositories {
    maven("https://maven.neuralnexus.dev/releases")
    maven("https://maven.neuralnexus.dev/snapshots")
    maven("https://maven.neuralnexus.dev/mirror")
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
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modName,
                "Specification-Version" to version,
                "Specification-Vendor" to "SomeVendor",
                "Implementation-Version" to version,
                "Implementation-Vendor" to "SomeVendor",
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

tasks.build.get().dependsOn("spotlessApply")

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
