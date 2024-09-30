package com.example.template.service

import com.example.template.common.exception.EntityNotFoundException
import com.example.template.firebase.Subscribing
import com.example.template.model.Subscribe
import com.example.template.model.User
import com.example.template.repository.SubscribeRepository
import com.example.template.repository.TokenRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class SubscribeService(
    private val subscribeRepository: SubscribeRepository,
    private val tokenRepository: TokenRepository,
){


    fun subscribe(targetType: String, targetId: Long, user: User){
        val subscribe = Subscribe.create(targetType, targetId, user.id!!)
        val topic = subscribe.topic()

        val tokens = tokenRepository.findAllByUserId(user.id!!)
        Subscribing.subscribe(
            tokens.map{ it.fcmToken},
            topic.value()
        )
    }

    fun unSubscribe(targetType: String, targetId: Long, user: User){
        val subscribe = subscribeRepository.findByUserIdAndTargetTypeAndTargetId(user.id!!, targetType, targetId)
            .getOrNull() ?: throw EntityNotFoundException()
        val topic = subscribe.topic()

        subscribeRepository.delete(subscribe)

        val tokens = tokenRepository.findAllByUserId(user.id!!)
        Subscribing.unsubscribe(
            tokens.map{ it.fcmToken },
            topic.value()
        )
    }

    fun updateSubscribe(user: User, token: String){
        val subscribes = subscribeRepository.findByUserId(user.id!!)
        val topics = subscribes.map(Subscribe::topic)
        topics.forEach { topic ->
            Subscribing.subscribe(
                listOf(token),
                topic.value()
            )
        }
    }


}