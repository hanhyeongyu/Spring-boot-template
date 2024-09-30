package com.example.template.application.command

data class SendNotificationToTopic(
    val title: String,
    val body: String,
    val targetType: String,
    val targetId: Long
)