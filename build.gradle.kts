plugins {
	war
	id("org.springframework.boot") version "3.2.7"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.dmeDocuments"
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
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("com.microsoft.sqlserver:mssql-jdbc")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
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