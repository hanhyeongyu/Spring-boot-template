package com.example.template

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = [
    "security.jwt.secret=local-context-test-secret-32-bytes!",
    "security.password.encoder.secret=local-context-test",
    "support.notification.enabled=false",
    "support.mail.enabled=false",
    "support.sms.enabled=false",
    "spring.profiles.active=local",
    "support.database.jdbc-url=jdbc:h2:mem:template-context-test;DB_CLOSE_DELAY=-1",
    "support.database.username=sa",
    "support.database.password=",
])
class TemplateApplicationTests {

    @Test
    fun contextLoads() {
    }

}
