package com.example.template.application


import com.example.template.application.command.*
import com.example.template.common.exception.EntityNotFoundException
import com.example.template.firebase.Messaging
import com.example.template.firebase.invalidToken
import com.example.template.model.Token
import com.example.template.model.TokenMonthExpireStrategy
import com.example.template.model.Topic
import com.example.template.repository.UserRepository
import com.example.template.service.SubscribeService
import com.example.template.service.TokenService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class NotificationApplication(
    private val tokenService: TokenService,
    private val subscribeService: SubscribeService,
    private val userRepository: UserRepository,
){

    /**
     * Send Notification
     */

    @Transactional
    fun handle(command: SendNotificationToTopic){
        val topic = Topic(command.targetType, command.targetId)

        val message = Messaging.commonMessageBuilder(command.title, command.body)
            .setTopic(topic.value())
            .build()
        Messaging.send(message)
    }

    @Transactional
    fun handle(command: SendNotificationToUser){
        val user = userRepository.findById(command.userId)
            .getOrNull()?: throw EntityNotFoundException()

        val tokens = tokenService.tokens(user)

        val message = Messaging.commonMulticastMessageBuilder(command.title, command.body)
            .addAllTokens(tokens.map(Token::fcmToken))
            .build()
        val batchResult = Messaging.send(message)


        batchResult.responses.forEachIndexed { index, sendResponse ->
            if(sendResponse.invalidToken()){
                val token = tokens[index]
                tokenService.delete(token)
            }
        }
    }


    /**
     *  Token
     */


    @Transactional
    fun handle(command: OnNewToken){
        val user = userRepository.findById(command.userId)
            .getOrNull()?: throw EntityNotFoundException()
        tokenService.add(command.token, user, command.platform)
        tokenService.removeExpireToken(user, TokenMonthExpireStrategy(2))
        subscribeService.updateSubscribe(user, command.token)
    }


    @Transactional
    fun handle(command: DeleteToken){
        tokenService.delete(command.token)
    }


    /**
     * Subscribe
     */


    @Transactional
    fun handle(command: Subscribe){
        val user = userRepository.findById(command.userId)
            .getOrNull()?: throw EntityNotFoundException()
        subscribeService.subscribe(command.targetType, command.targetId, user)
    }

    @Transactional
    fun handle(command: UnSubscribe){
        val user = userRepository.findById(command.userId)
            .getOrNull()?: throw EntityNotFoundException()
        subscribeService.unSubscribe(command.targetType, command.targetId, user)
    }

}