package com.example.template.service

import com.example.template.model.Platform
import com.example.template.model.Token
import com.example.template.model.TokenExpireStrategy
import com.example.template.model.User
import com.example.template.repository.TokenRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class TokenService(
    private val tokenRepository: TokenRepository
){

    fun tokens(user: User): List<Token> {
        return tokenRepository.findAllByUserId(user.id!!)
    }

    fun add(token: String, user: User, platform: Platform){
        val fcmToken = Token.create(
            value = token,
            userId = user.id!!,
            platform = platform
        )
        tokenRepository.save(fcmToken)
    }

    fun delete(token: String){
        val fcmToken = tokenRepository.findByFcmToken(token)
            .getOrNull() ?: return
        tokenRepository.delete(fcmToken)
    }

    fun delete(token: Token){
        tokenRepository.delete(token)
    }



    fun removeExpireToken(user: User, strategy: TokenExpireStrategy) {
        val fcmTokens = tokenRepository.findAllByUserId(user.id!!)
        val expireTokens = fcmTokens.filter { token ->
            strategy.isExpire(token)
        }
        tokenRepository.deleteAll(expireTokens)
    }

}