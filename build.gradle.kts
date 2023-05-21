plugins {
    val kotlinVersion = "1.7.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "per.autumn.mirai.autoreply"
version = "2.0.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies {
    implementation("cn.hutool:hutool-all:5.8.11")
    implementation("net.objecthunter:exp4j:0.4.8")
    testImplementation(kotlin("test"))
}
tasks.test {
    useJUnitPlatform()
}

