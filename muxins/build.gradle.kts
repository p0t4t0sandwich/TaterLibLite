import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

plugins {
    alias(libs.plugins.shadow)
}

base {
    archivesName = "muxins"
}

val mixinShaded: SourceSet by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.asm.tree)

    compileOnly(project(":base"))
    compileOnly(project(":metadata"))
}

var relocatedMixinJar = tasks.register<ShadowJar>("relocatedMixinJar") {
    archiveClassifier.set("relocated")
    from(sourceSets.main.get().output)
    relocate("dev.neuralnexus.taterapi.muxins", "dev.neuralnexus.taterapi.muxins.shaded")
    relocate("org.objectweb.asm", " org.spongepowered.asm.lib")
}

tasks.jar {
    dependsOn(relocatedMixinJar)
    from(
        sourceSets.main.get().output,
        zipTree(relocatedMixinJar.get().archiveFile.get().asFile)
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "TaterLibLite Muxins",
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
            .replace("muxins", "dev/neuralnexus/taterlib/lite/jvmdg")
    }
}
