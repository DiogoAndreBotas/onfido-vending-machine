plugins {
	val kotlinJvmPluginVersion = "1.9.25"
	val kotlinSpringPluginVersion = "1.9.25"
	val kotlinSerializationPluginVersion = "2.0.20"
	val springBootPluginVersion = "3.4.0"
	val springDependencyManagementPluginVersion = "1.1.6"

	kotlin("jvm") version kotlinJvmPluginVersion
	kotlin("plugin.spring") version kotlinSpringPluginVersion
	kotlin("plugin.serialization") version kotlinSerializationPluginVersion
	id("org.springframework.boot") version springBootPluginVersion
	id("io.spring.dependency-management") version springDependencyManagementPluginVersion
}

group = "diogoandrebotas.onfido"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

val springBootVersion = "3.4.0"
val postgresqlVersion = "42.7.4"
val kotlinReflectVersion = "2.1.0"
val kotlinxSerializationJsonVersion = "1.7.3"
val kotlinTestJUnitJupiterVersion = "2.1.0"
val junitJupiterApiVersion = "5.11.3"
val mockitoVersion = "5.14.2"
val mockitoKotlinVersion = "5.4.0"
val testContainersVersion = "1.20.4"
val junitPlatformLauncher = "1.11.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
	implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
	implementation("org.postgresql:postgresql:$postgresqlVersion")
	implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinTestJUnitJupiterVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterApiVersion")
	testImplementation("org.mockito:mockito-core:$mockitoVersion")
	testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
	testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
	testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
	testImplementation("org.testcontainers:postgresql:$testContainersVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformLauncher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
