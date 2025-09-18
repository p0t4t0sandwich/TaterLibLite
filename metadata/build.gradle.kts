import java.time.Instant

base {
    archivesName = "metadata"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(project(":base"))
    testRuntimeOnly(project(":base"))

    compileOnly(libs.mixin)
    compileOnly(libs.asm.tree)

    compileOnly("space.vectrix.ignite:ignite-api:1.1.0")

    compileOnly(project(":base"))

    // TODO: Replace with proper source sets
    compileOnly("dev.neuralnexus:entrypoint-spoof:0.1.26")
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "1G"
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "TaterLibLite Metadata",
                "Specification-Version" to version,
                "Specification-Vendor" to "NeualNexus",
                "Implementation-Version" to version,
                "Implementation-Vendor" to "NeualNexus",
                "Implementation-Timestamp" to Instant.now().toString()
            )
        )
    }
}
tasks.build.get().dependsOn(tasks.test)
