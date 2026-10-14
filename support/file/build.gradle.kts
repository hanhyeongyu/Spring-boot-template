dependencies{
    implementation("software.amazon.awssdk:s3:2.20.100")
    implementation("software.amazon.awssdk:auth:2.20.100")
    implementation("software.amazon.awssdk:aws-core:2.20.100")
    implementation(project(":platform"))

    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-tx")
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
}