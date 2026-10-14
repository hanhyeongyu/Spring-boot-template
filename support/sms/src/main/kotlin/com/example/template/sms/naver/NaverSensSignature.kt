package com.example.template.sms.naver

import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal object NaverSensSignature {
    fun sign(
        secretKey: String,
        method: String,
        path: String,
        timestampMillis: String,
        accessKey: String,
    ): String {
        val message = "$method $path\n$timestampMillis\n$accessKey"
        val signingKey = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)
        val rawHmac = mac.doFinal(message.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(rawHmac)
    }
}

