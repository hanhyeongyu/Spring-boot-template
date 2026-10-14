package com.example.template.sms.naver

import com.example.template.common.exception.ServiceUnavailableException
import com.example.template.sms.SmsSender
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import java.time.Duration

@Component
@ConditionalOnProperty(name = ["support.sms.enabled"], havingValue = "true")
@EnableConfigurationProperties(NaverSensSmsProperties::class)
class NaverSensSmsSender(
    private val properties: NaverSensSmsProperties,
    restTemplateBuilder: RestTemplateBuilder,
) : SmsSender {
    private val restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(3))
        .setReadTimeout(Duration.ofSeconds(5))
        .build()

    override fun send(toE164: String, content: String) {
        require(toE164.trim().matches(Regex("(?:\\+82|0)1[0-9]{8,9}"))) {
            "Naver SENS sender requires a Korean mobile number"
        }
        val to = toE164.trim().removePrefix("+82").removePrefix("82").removePrefix("0").let { "0$it" }
        val timestampMillis = System.currentTimeMillis().toString()
        val path = "/sms/v2/services/${properties.serviceId}/messages"
        val url = "https://sens.apigw.ntruss.com$path"

        val signature = NaverSensSignature.sign(
            secretKey = properties.secretKey,
            method = "POST",
            path = path,
            timestampMillis = timestampMillis,
            accessKey = properties.accessKey,
        )

        val headers = HttpHeaders().apply {
            add("x-ncp-apigw-timestamp", timestampMillis)
            add("x-ncp-iam-access-key", properties.accessKey)
            add("x-ncp-apigw-signature-v2", signature)
            contentType = MediaType.APPLICATION_JSON
        }

        val body = mapOf(
            "type" to "SMS",
            "countryCode" to "82",
            "from" to properties.from,
            "content" to content,
            "messages" to listOf(mapOf("to" to to)),
        )

        val entity = HttpEntity(body, headers)
        val response = try {
            restTemplate.postForEntity(url, entity, String::class.java)
        } catch (exception: RestClientException) {
            throw ServiceUnavailableException("SENS SMS request failed: ${exception.message}")
        }
        if (response.statusCode.value() != 202) {
            throw ServiceUnavailableException("SENS SMS request failed: ${response.statusCode.value()}")
        }
    }

}
