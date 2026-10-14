package com.example.template.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder

@Configuration
internal class PasswordConfiguration {
    @Bean
    fun passwordEncoder(
        @Value("\${security.password.encoder.secret}")
        passwordEncoderSecret: String
    ): PasswordEncoder {
        return Pbkdf2PasswordEncoder(
            passwordEncoderSecret,
            128,
            100,
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        )
    }



}
