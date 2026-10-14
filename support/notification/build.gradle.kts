group = "${rootProject.group}.support"

base { archivesName.set("support-notification") }

dependencies {
    implementation("com.google.firebase:firebase-admin:${property("firebaseAdminVersion")}")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
}

tasks.processResources { exclude("firebase-service-account.json") }
