plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.11.3"

    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
}

group = "santannaf.demo.spring-native.open-telemetry"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

//dependencyManagement {
//    imports {
//        mavenBom("io.opentelemetry:opentelemetry-bom:1.56.0")
//        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.22.0")
//        mavenBom("io.micrometer:micrometer-tracing-bom:1.6.0")
//    }
//}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-restclient")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

//    // Metrics
//    implementation("io.micrometer:micrometer-registry-otlp")
//    implementation("io.micrometer:micrometer-tracing-bridge-otel")
//    implementation("io.micrometer:micrometer-tracing")

//    // Traces
//    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
//    implementation("io.opentelemetry:opentelemetry-extension-kotlin")

    // Traces and some metrics
//    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
//    implementation("io.opentelemetry.contrib:opentelemetry-samplers:1.45.0-alpha")

    // Unit Tests
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-opentelemetry-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
//        jvmTarget.set(JvmTarget.JVM_25)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
