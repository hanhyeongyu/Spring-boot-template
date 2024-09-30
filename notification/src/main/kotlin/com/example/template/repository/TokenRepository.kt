package com.example.template.repository

import com.example.template.model.Token
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenRepository: JpaRepository<Token, Long>{
    fun findByFcmToken(value: String): Optional<Token>
    fun findAllByUserId(userId: Long): List<Token>
}