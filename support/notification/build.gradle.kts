group = "${rootProject.group}.support"

base { archivesName.set("support-notification") }

dependencies {
    implementation("com.google.firebase:firebase-admin:${property("firebaseAdminVersion")}")
    compileOnly("org.springframework:spring-context")
}
