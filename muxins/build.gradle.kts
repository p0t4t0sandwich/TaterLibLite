import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

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

    compileOnly("com.github.LegacyModdingMC.UniMixins:unimixins-all-1.7.10:0.3.0:dev")
}

tasks.jar {
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
