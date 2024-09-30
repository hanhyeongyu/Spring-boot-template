package com.example.template.application.command

import com.example.template.model.Platform


data class OnNewToken(
    val token: String,
    val userId: Long,
    val platform: Platform
)