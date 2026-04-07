import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

plugins {
    alias(libs.plugins.shadow)
}

base {
    archivesName = "base"
}

val relocatedMixin: SourceSet by sourceSets.creating
sourceSets.main {
    compileClasspath += relocatedMixin.output
    runtimeClasspath += relocatedMixin.output
}
val relocatedMixinCompileOnly: Configuration by configurations.getting

dependencies {
    compileOnly(libs.asm.tree)
    compileOnly(libs.gson)
    compileOnly(libs.mixin)
    relocatedMixinCompileOnly(libs.jspecify)
    relocatedMixinCompileOnly(libs.asm.tree)
    relocatedMixinCompileOnly(libs.gson)
    relocatedMixinCompileOnly(libs.mixin)
}

var relocatedMixinJar = tasks.register<ShadowJar>("relocatedMixinJar") {
    archiveClassifier.set("relocated")
    from(relocatedMixin.output)
    relocate("org.objectweb.asm", " org.spongepowered.asm.lib")
}

tasks.jar {
    dependsOn(relocatedMixinJar)
    from(
        sourceSets.main.get().output,
        zipTree(relocatedMixinJar.get().archiveFile.get().asFile)
    )
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "TaterLibLite Base",
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
            .replace("base", "dev/neuralnexus/taterlib/lite/jvmdg")
    }
}
