package com.example.template.firebase

import com.google.firebase.messaging.*


object Messaging {

    fun sendToTopic(title: String, body: String, topic: String) {
        val message = commonMessageBuilder(title, body).setTopic(topic).build()
        FirebaseMessaging.getInstance().send(message)
    }

    fun sendToTokens(title: String, body: String, tokens: List<String>): List<TokenSendResult> {
        val message = commonMulticastMessageBuilder(title, body).addAllTokens(tokens).build()
        return FirebaseMessaging.getInstance().sendEachForMulticast(message).responses.map {
            TokenSendResult(invalidToken = it.invalidToken())
        }
    }

    /**
     * Builds the apns config that will customize how a message is received on iOS.
     *
     * @return apns config of an FCM request.
     */

    private fun apnsConfig(title: String, body: String): ApnsConfig {
        val apsAlert = ApsAlert.builder()
            .setTitle(title)
            .setBody(body)

            .build()

        val aps = Aps.builder()
            .setBadge(1)
            .setAlert(apsAlert)
            .build()

        return ApnsConfig.builder()
            .putHeader("apns-priority", "10")
            .setAps(aps)
            .build()
    }


    /**
     * Builds the android config that will customize how a message is received on Android.
     *
     * @return android config of an FCM request.
     */

    private fun androidConfig(title: String, body: String): AndroidConfig {
        val androidNotification = AndroidNotification.builder()
            .setClickAction("android.intent.action.MAIN")
            .setTitle(title)
            .setBody(body)
            .build()



        return AndroidConfig.builder()
            .setNotification(androidNotification)
            .build()
    }

    /**
     * Common
     */

    private fun commonMessageBuilder(title: String, body: String): Message.Builder{
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        return Message.builder()
            .setNotification(notification)
            .setApnsConfig(apnsConfig(title, body))
            .setAndroidConfig(androidConfig(title, body))
    }

    private fun commonMulticastMessageBuilder(title: String, body: String): MulticastMessage.Builder{
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        return MulticastMessage.builder()
            .setNotification(notification)
            .setApnsConfig(apnsConfig(title, body))
            .setAndroidConfig(androidConfig(title, body))
    }




}