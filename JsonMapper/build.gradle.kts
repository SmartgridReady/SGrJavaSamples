import java.net.URL

plugins {
    id("java")
}

group = "ch.smartgridreay"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        url = uri("https://nexus.library.smartgridready.ch/repository/maven-releases/")
    }
}

dependencies {

    implementation ("ch.smartgridready:commhandler4modbus:1.0.1") {
        exclude (group = "SGrJava.InterfaceFactory", module = "emfEI4Modbus")
    }

    implementation (group = "org.eclipse.emf", name = "org.eclipse.emf.ecore", version = "2.24.0")
    implementation (group = "org.eclipse.emf", name = "org.eclipse.emf.ecore.xmi", version = "2.16.0");
    implementation (group = "org.eclipse.emf", name = "org.eclipse.emf.common", version = "2.24.0");
    implementation (group = "org.eclipse.emf", name = "org.eclipse.emf.edit", version = "2.17.0")
    implementation("commons-io:commons-io:2.15.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation ("io.burt:jmespath-core:0.5.1")
    implementation ("io.burt:jmespath-jackson:0.5.1")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")



    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
