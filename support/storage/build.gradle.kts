dependencies{
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.springframework.boot:spring-boot-starter-aop")

    runtimeOnly("com.h2database:h2")
    runtimeOnly ("com.mysql:mysql-connector-j")
}