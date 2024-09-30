package com.example.template.model

import com.example.template.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime


@Entity
class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var fcmToken: String,
    val userId: Long,
    val timeStamp: LocalDateTime,
    val platform: Platform
): BaseEntity(){


    companion object {
        fun create(value: String, userId: Long, platform: Platform): Token{
            return Token(
                fcmToken = value,
                userId = userId,
                timeStamp = LocalDateTime.now(),
                platform = platform
            )
        }
    }

    override fun hashCode(): Int {
        return fcmToken.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Token

        if (fcmToken != other.fcmToken) return false

        return true
    }
}