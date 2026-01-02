import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

base {
    archivesName = "core"
}

dependencies {
    compileOnly(libs.mojang.authlib)
    compileOnly(libs.guava)
    compileOnly(libs.mixin)
    compileOnly(libs.netty.buffer)
    compileOnly(libs.netty.codec)
    compileOnly(libs.asm.tree)

    compileOnly("dev.neuralnexus:entrypoint-spoof:0.1.26")

    compileOnly(project(":base"))
    compileOnly(project(":metadata"))
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "TaterLibLite Core",
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
            .replace("core", "dev/neuralnexus/taterlib/lite/jvmdg")
    }
}
