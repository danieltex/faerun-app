import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.10"
	kotlin("plugin.spring") version "1.5.10"
	kotlin("plugin.jpa") version "1.5.10"
	jacoco
}

group = "com.github.danieltex"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
//	runtimeOnly("mysql:mysql-connector-java")
	implementation("javax.validation", "validation-api", "2.0.1.Final")
	implementation("org.hibernate.validator", "hibernate-validator", "7.0.1.Final")

	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
	reports {
		xml.isEnabled = true
		csv.isEnabled = false
		html.isEnabled = true
		html.destination = file("$buildDir/reports/coverage")
	}
	dependsOn(tasks.test)
	finalizedBy(tasks.jacocoTestCoverageVerification)
	classDirectories.setFrom(
		sourceSets.main.get().output.asFileTree.matching {
			exclude(
				"com/github/danieltex/faerunapp/dtos/**",
				"com/github/danieltex/faerunapp/entities/**"
			)
		}
	)
}


allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

jacoco {
	toolVersion = "0.8.7"
}
