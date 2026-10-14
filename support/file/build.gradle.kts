dependencies{
    implementation("software.amazon.awssdk:s3:2.20.100")
    implementation("software.amazon.awssdk:auth:2.20.100")
    implementation("software.amazon.awssdk:aws-core:2.20.100")
    implementation(project(":platform"))

    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")
}