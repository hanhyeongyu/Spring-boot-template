package com.example.template

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@SpringBootTest(properties = [
    "security.jwt.secret=local-context-test-secret-32-bytes!",
    "security.password.encoder.secret=local-context-test",
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

    // The template ships without a Firebase service account file.
    @TestConfiguration
    class WithoutFirebase {
        @Bean
        fun removeFirebaseBeans() = BeanDefinitionRegistryPostProcessor { registry ->
            listOf("firebaseConfig", "initializeFirebase").forEach(registry::removeBeanDefinition)
        }
    }
}
