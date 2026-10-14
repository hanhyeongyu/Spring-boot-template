package com.example.template.jwt

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.JwtValidationException
import java.time.Duration
import javax.crypto.spec.SecretKeySpec

class JwtProviderTest {
    private val provider = JwtProvider(SecretKeySpec(ByteArray(32) { 7 }, "HmacSHA256"), "test", 30, 60)

    @Test fun `should reject refresh token for access authentication`() {
        val issued = provider.compose("1", listOf("MEMBER"))
        assertThat(provider.decode(issued.accessToken).subject).isEqualTo("1")
        assertThatThrownBy { provider.decode(issued.refreshToken) }.isInstanceOf(JwtValidationException::class.java)
    }

    @Test fun `should reject access token for refresh`() {
        val issued = provider.compose("1", listOf("MEMBER"))
        assertThatThrownBy { provider.decodeRefresh(issued.accessToken) }.isInstanceOf(JwtValidationException::class.java)
    }

    @Test fun `should honor configured minutes for both expiration times`() {
        val issued = provider.compose("1", listOf("MEMBER"))
        val refresh = provider.decodeRefresh(issued.refreshToken)
        assertThat(issued.expiresIn).isEqualTo(Duration.ofMinutes(30).toMillis())
        assertThat(issued.refreshExpiresIn).isEqualTo(Duration.ofMinutes(60).toMillis())
        assertThat(Duration.between(refresh.issuedAt, refresh.expiresAt)).isEqualTo(Duration.ofMinutes(60))
    }

    @Test fun `should preserve role names when refreshing`() {
        val issued = provider.compose("1", listOf("TEAM_ADMIN"))
        val refreshed = provider.compose(issued.refreshToken)
        assertThat(provider.decode(refreshed.accessToken).getClaimAsString("roles")).isEqualTo("ROLE_TEAM_ADMIN")
        assertThat(refreshed.refreshToken).isEqualTo(issued.refreshToken)
        assertThat(refreshed.refreshExpiresIn).isLessThanOrEqualTo(issued.refreshExpiresIn)
    }
}
