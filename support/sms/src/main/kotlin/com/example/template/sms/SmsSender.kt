package com.example.template.sms

interface SmsSender {
    fun send(toE164: String, content: String)
}

