plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.1"
}

group = "per.autumn.mirai.autoreply"
version = "1.3"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies {
    implementation("cn.hutool:hutool-core:5.8.0.M1")
    implementation ("net.objecthunter:exp4j:0.4.8")
}

