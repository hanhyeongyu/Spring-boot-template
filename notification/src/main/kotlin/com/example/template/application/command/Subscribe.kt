package com.example.template.application.command

data class Subscribe(
    val targetType: String,
    val targetId: Long,
    val userId: Long
)