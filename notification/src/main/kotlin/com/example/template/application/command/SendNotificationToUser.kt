package com.example.template.application.command

data class SendNotificationToUser(
    val title: String,
    val body: String,
    val userId: Long
)


