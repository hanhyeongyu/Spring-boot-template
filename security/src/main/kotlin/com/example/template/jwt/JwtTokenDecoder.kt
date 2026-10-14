package com.example.template.jwt

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder

interface JwtTokenDecoder : JwtDecoder {
    fun decodeRefresh(token: String): Jwt
}
