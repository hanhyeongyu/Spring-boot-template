package com.example.template.mail.spring

import com.example.template.mail.EmailSender
import jakarta.mail.internet.InternetAddress
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Component
@ConditionalOnProperty(name = ["support.mail.enabled"], havingValue = "true")
class SpringEmailSender(
    private val javaMailSender: JavaMailSender,
) : EmailSender {
    override fun sendHtml(to: String, subject: String, html: String, fromName: String, fromEmail: String) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, false, "UTF-8")
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(html, true)
        helper.setFrom(InternetAddress(fromEmail, fromName))
        javaMailSender.send(message)
    }
}

