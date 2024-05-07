plugins {
    id("java")
}

group = "dev.goldenstack.bedwars"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://jitpack.io")
}

dependencies {
    implementation("net.minestom:minestom-snapshots:1_20_5-323c75f8a5")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}