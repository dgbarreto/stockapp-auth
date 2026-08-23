plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.sonarqube)
}

val localProperties = java.util.Properties().apply {
    val f = file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val useLocalDesignSystem = localProperties.getProperty("useLocalDesignSystem", "false").toBoolean()

sonar {
    properties {
        property("sonar.projectKey", "dgbarreto_stockapp-auth")
        property("sonar.organization", "dgbarreto")
    }
}

// Demo/fixture modules, not product code — excluded from analysis.
project(":sample") {
    sonar {
        isSkipProject = true
    }
}
project(":sample-android") {
    sonar {
        isSkipProject = true
    }
}

allprojects {
    // Gradle's dependency locking is incompatible with composite-build substitution
    // (gradle/gradle#4749, #28856): resolving a locked native (klib) configuration
    // while `useLocalDesignSystem` substitutes designsystem via includeBuild() throws
    // UnsupportedOperationException in DefaultDependencyLockingProvider. Locking a
    // pinned artifact version is meaningless anyway when depending on the local,
    // unpublished designsystem build, so skip it in that mode.
    //
    // :sample and :sample-android are also skipped: Compose Multiplatform resolves
    // a host-OS-specific desktop artifact (e.g. desktop-jvm-macos-arm64 vs
    // desktop-jvm-linux-x64) for the same configuration, so a lock file committed
    // from one machine's OS/arch fails verification on every other one.
    if (!useLocalDesignSystem && name != "sample" && name != "sample-android") {
        dependencyLocking {
            lockAllConfigurations()
        }

        // Compose Hot Reload's "Dev" configurations (desktopDevCompileClasspath,
        // composeHotReloadDevDesktopDevRuntimeClasspath, ...) resolve a host-OS-specific
        // desktop-jvm artifact (linux-x64 vs macos-arm64 vs ...), so a lock file
        // committed from one machine's OS/arch never matches on another — same class
        // of problem as :sample above, but this one hits :auth itself since Hot
        // Reload's tooling is wired into every module, not just the demo app.
        configurations.configureEach {
            if (name.contains("Dev")) {
                resolutionStrategy.deactivateDependencyLocking()
            }
        }
    }
}
