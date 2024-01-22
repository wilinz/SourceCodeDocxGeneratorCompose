import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.wilinz.scdgc"
version = "1.0-SNAPSHOT"

val compose_version = properties["compose.version"]

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.apache.poi:poi-ooxml:3.17"){
        exclude(group = "org.apache.xmlbeans", module = "xmlbeans")
    }
    implementation("org.apache.poi:ooxml-schemas:1.3")
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    // https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended-desktop
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.Pkg)
            packageName = "SourceCodeDocxGeneratorCompose"
            packageVersion = "1.0.0"
            modules("jdk.unsupported")
            macOS {
                iconFile.set(project.file("logo/logo.icns"))
            }
            windows {
                iconFile.set(project.file("logo/logo.ico"))
            }
            linux {
                iconFile.set(project.file("logo/logo.png"))
            }
            buildTypes.release.proguard {
                version.set("7.3.2")
                configurationFiles.from(project.file("proguard-rules.pro"))
                isEnabled.set(false)
                obfuscate.set(false)
            }
        }
    }
}
