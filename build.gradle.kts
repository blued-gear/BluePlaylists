plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("maven-publish")
}

group = "apps.chocolatecakecodes.bluebeats"
version = "0.1.0"

kotlin {
    jvm()
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlin.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

publishing {
    repositories {
        mavenLocal {
            url = uri(layout.projectDirectory.dir("mavenrepo"))
        }
    }
}
