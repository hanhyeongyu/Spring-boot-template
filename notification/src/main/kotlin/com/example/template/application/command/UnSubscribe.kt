package com.example.template.application.command

data class UnSubscribe(
    val targetType: String,
    val targetId: Long,
    val userId: Long
)