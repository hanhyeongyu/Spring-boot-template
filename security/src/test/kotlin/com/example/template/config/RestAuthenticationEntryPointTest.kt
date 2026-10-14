package com.example.template.config

import com.example.template.jwt.JwtProvider
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import java.time.Duration
import java.time.Instant
import java.util.Date
import javax.crypto.spec.SecretKeySpec

class RestAuthenticationEntryPointTest {
    private val key = SecretKeySpec(ByteArray(32) { 7 }, "HmacSHA256")
    private val provider = JwtProvider(key, "test", 30, 60)
    private val authenticationProvider = JwtAuthenticationProvider(provider)
    private val objectMapper = jacksonObjectMapper()
    private val entryPoint = RestAuthenticationEntryPoint(objectMapper)

    @Test fun `should respond 4011 when access token is expired`() {
        val expiredAt = Instant.now().minus(Duration.ofHours(1))
        val expired = Jwts.builder().signWith(key).setIssuer("test").setSubject("1")
            .setIssuedAt(Date.from(expiredAt.minus(Duration.ofMinutes(30)))).setExpiration(Date.from(expiredAt))
            .claim("roles", "ROLE_MEMBER").claim("token_use", "access").compact()

        assertResponse(authenticationFailure(expired), expectedCode = 4011)
    }

    @Test fun `should respond 4013 when access token is invalid`() {
        val refreshToken = provider.compose("1", listOf("MEMBER")).refreshToken

        assertResponse(authenticationFailure(refreshToken), expectedCode = 4013)
    }

    @Test fun `should respond 4010 when bearer token is missing`() {
        assertResponse(InsufficientAuthenticationException("Full authentication is required"), expectedCode = 4010)
    }

    private fun authenticationFailure(token: String): AuthenticationException =
        assertThrows { authenticationProvider.authenticate(BearerTokenAuthenticationToken(token)) }

    private fun assertResponse(exception: AuthenticationException, expectedCode: Int) {
        val response = MockHttpServletResponse()
        entryPoint.commence(MockHttpServletRequest(), response, exception)

        assertThat(response.status).isEqualTo(401)
        assertThat(objectMapper.readTree(response.contentAsString)["code"].asInt()).isEqualTo(expectedCode)
    }
}
