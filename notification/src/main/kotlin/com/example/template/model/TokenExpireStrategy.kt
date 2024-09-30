package com.example.template.model

import java.time.LocalDate


interface TokenExpireStrategy {
    fun isExpire(token: Token): Boolean
}


class TokenMonthExpireStrategy(
    private val month: Long
): TokenExpireStrategy {
    override fun isExpire(token: Token): Boolean {
        val today = LocalDate.now()
        val expireAt: LocalDate = token.timeStamp.plusMonths(month).toLocalDate()
        return today.isAfter(expireAt)
    }
}