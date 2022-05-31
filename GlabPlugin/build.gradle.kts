
val name: String by project
val ideaVersion: String by project

plugins {
    id("java")
    id("idea")
    id("org.jetbrains.intellij") version "1.5.3"
    id("io.franzbecker.gradle-lombok") version "5.0.0"
}

group = "ru.itis"
version = "1.0.0"


repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
//    maven(
//            url = "https://oss.sonatype.org/content/repositories/snapshots/"
//    )
//    maven(
//            url = "https://packages.jetbrains.team/maven/p/intellij-plugin-verifier/intellij-plugin-structure"
//    )
//    maven(
//            url = "https://www.jetbrains.com/intellij-repository/releases"
//    )
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.springframework:spring-web:5.3.19")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
}


// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(name)
    version.set(ideaVersion)
    type.set("IU") // Target IDE Platform

    plugins.set(listOf("git4idea"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("212")
        untilBuild.set("222.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

//    runIde {
//        jvmArgs("--add-exports java.base/jdk.internal.vm=ALL-UNNAMED")
//    }
}

//,"--add-exports java.base/jdk.internal.vm=ALL-UNNAMED"
