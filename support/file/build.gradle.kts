dependencies{
    implementation(platform("software.amazon.awssdk:bom:2.20.100"))
    implementation("software.amazon.awssdk:s3")
    implementation(project(":platform"))

    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")
}