plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "diogoandrebotas.onfido"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation(platform("org.http4k:http4k-bom:5.31.0.0"))
	implementation("org.http4k:http4k-core:5.38.0.0")
	implementation("org.http4k:http4k-client-apache:5.38.0.0")
	implementation("org.http4k:http4k-format-jackson:5.38.0.0")
	implementation("org.http4k:http4k-format-moshi:5.38.0.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<JavaExec>("bootRun") {
	standardInput = System.`in`
}
