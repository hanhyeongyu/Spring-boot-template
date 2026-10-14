package com.example.template.jwt

import org.springframework.security.oauth2.jwt.BadJwtException

class JwtExpiredException(cause: Throwable) : BadJwtException("Jwt expired", cause)
