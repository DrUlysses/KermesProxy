import org.gradle.kotlin.dsl.kotlin

import gobley.gradle.GobleyHost
import gobley.gradle.rust.dsl.hostNativeTarget
import gobley.gradle.rust.targets.RustPosixTarget

val resolvedVersion = (findProperty("version") as? String)?.takeIf { it != "unspecified" } ?: "1.0.0"
version = resolvedVersion
group = "kermes.proxy"

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.undercouch.download)
    alias(libs.plugins.serialization)
    alias(libs.plugins.gobley.cargo)
    alias(libs.plugins.gobley.uniffi)
    alias(libs.plugins.kotlin.atomicfu)
    alias(libs.plugins.squareup.wire)
    alias(libs.plugins.kotlinx.rpc)
}

wire {
    sourcePath {
        srcDir(layout.projectDirectory.dir("src").dir("commonMain").dir("proto"))
    }

    kotlin {
        rpcRole = "server"
        rpcCallStyle = "suspending"
        singleMethodServices = false
    }
}

kotlin {
    linuxX64 {
        binaries {
            all {
                linkerOpts(
                    "-L/usr/lib",
                    "-L/usr/lib/x86_64-linux-gnu"
                )
            }
        }
    }
    mingwX64 {
        binaries {
            all {
                linkerOpts(
                    "-Wl,--subsystem,windows",
                    "-lversion",
                    "-lmsi",
                    "-lwtsapi32",
                    "-luserenv",
                    "-ladvapi32"
                )
            }
        }
    }

    val nativeTargets = listOfNotNull(
        hostNativeTarget(),
        // Build for Linux on Windows as well
        if (GobleyHost.Platform.Windows.isCurrent) {
            linuxX64()
        } else {
            null
        }
    )

    nativeTargets.forEach { target ->
        target.binaries {
            executable {
                entryPoint = "$group.main"
            }
        }
    }

    compilerOptions.optIn.addAll(
        "kotlin.uuid.ExperimentalUuidApi",
        "kotlin.time.ExperimentalTime",
        "kotlinx.cinterop.ExperimentalForeignApi",
        "kotlin.experimental.ExperimentalNativeApi",
        "kotlin.native.internal.InternalForKotlinNative",
        "kotlin.concurrent.atomics.ExperimentalAtomicApi"
    )

    sourceSets {
        commonMain {
            resources.srcDirs("resources")
            dependencies {
                implementation(libs.bundles.ktor.client)
                implementation(libs.bundles.ktor.server)
                implementation(libs.ktor.network)
                implementation(libs.ktor.network.tls)
                implementation(libs.bundles.ksoup)
                implementation(libs.bundles.rpc)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.io)
                implementation(libs.kotlin.logging)
                implementation(libs.squareup.wire)
            }
        }
        linuxMain.dependencies {
            implementation(libs.ktor.client.curl)
        }
        mingwMain.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
    }
}

uniffi {
    formatCode = true
    generateFromLibrary {
        // Make UniFFI generate bindings using the Linux X64 build on Windows
        if (GobleyHost.Platform.Windows.isCurrent) {
            build = RustPosixTarget.LinuxX64
        }
    }
}

// To update, use `gradle wrapper --gradle-version %VERSION_HERE% --distribution-type bin`
tasks.withType<Wrapper> {
    gradleVersion = libs.versions.gradle.get()
    distributionType = Wrapper.DistributionType.BIN
}

tasks.configureEach {
    if (name.startsWith("compile") &&
        name.contains(
            other = "kotlin",
            ignoreCase = true
        )
    ) {
        dependsOn("generateCommonMainProtos")
    }
}
