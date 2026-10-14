package com.example.template.sms.naver

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "naver.sens.sms")
data class NaverSensSmsProperties(
    val accessKey: String,
    val secretKey: String,
    val serviceId: String,
    val from: String,
)

