import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

plugins {
//    alias(libs.plugins.unimined)
}

base {
    archivesName = "network"
}

val main: SourceSet by sourceSets.getting
val common: SourceSet by sourceSets.creating
val mainCompileOnly: Configuration by configurations.creating
configurations.compileOnly.get().extendsFrom(mainCompileOnly)
val commonCompileOnly: Configuration by configurations.getting

//unimined.footgunChecks = false

//unimined.minecraft(common) {
//    version("26.1.2")
//    if (sourceSet == common) {
//        defaultRemapJar = false
//    }
//}

//unimined.minecraft(forge1171) {
//    combineWith(common)
//    version("1.17.1")
//    mappings {
//        parchment("1.17.1", "2021.12.12")
//        mojmap()
//        devFallbackNamespace("official")
//    }
//    minecraftForge {
//        loader("37.1.1")
//    }
//}

dependencies {
    mainCompileOnly(libs.guava)
    mainCompileOnly(libs.netty.buffer)
    mainCompileOnly(libs.netty.codec)

    mainCompileOnly(libs.jspecify)
    mainCompileOnly(project(":base"))
    mainCompileOnly(project(":core"))
    mainCompileOnly(project(":metadata"))

    commonCompileOnly(libs.jspecify)
    commonCompileOnly(project(":base"))
    commonCompileOnly(project(":core"))
    commonCompileOnly(project(":metadata"))
}

tasks.jar {
    from(main.output, common.output)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "TaterLibLite Network",
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
            .replace("network", "dev/neuralnexus/taterlib/lite/jvmdg")
    }
}
