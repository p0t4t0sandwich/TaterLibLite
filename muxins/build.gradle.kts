import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

base {
    archivesName = "muxins"
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.asm.tree)

    compileOnly(project(":base"))
    compileOnly(project(":metadata"))
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
