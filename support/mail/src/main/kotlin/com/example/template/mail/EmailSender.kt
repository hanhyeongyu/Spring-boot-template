package com.example.template.mail

interface EmailSender {
    fun sendHtml(to: String, subject: String, html: String, fromName: String, fromEmail: String)
}

