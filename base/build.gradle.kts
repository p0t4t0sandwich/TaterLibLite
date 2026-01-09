import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

base {
    archivesName = "base"
}

dependencies {
    compileOnly(libs.asm.tree)
    compileOnly(libs.gson)
    compileOnly(libs.mixin)
}

tasks.jar {
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
