package com.example.template.jwt

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtIssuerValidator
import org.springframework.security.oauth2.jwt.JwtTimestampValidator
import org.springframework.security.oauth2.jwt.JwtValidationException
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.Date
import javax.crypto.spec.SecretKeySpec

@Component
internal class JwtProvider(
    private val key: SecretKeySpec,
    private val issuer: String,
    accessMinutes: Long,
    refreshMinutes: Long,
) : JwtComposer, JwtTokenDecoder, Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer> {
    private val accessExpiration = Duration.ofMinutes(accessMinutes).toMillis()
    private val refreshExpiration = Duration.ofMinutes(refreshMinutes).toMillis()
    private val decoder = NimbusJwtDecoder.withSecretKey(key).build().apply {
        setJwtValidator(DelegatingOAuth2TokenValidator(expirationValidator(), JwtIssuerValidator(issuer)))
    }

    init { require(accessMinutes > 0 && refreshMinutes >= accessMinutes) }

    @Autowired
    constructor(
        @Value("\${security.jwt.secret}") secret: String,
        @Value("\${security.jwt.issue}") issuer: String,
        @Value("\${security.jwt.expiration.access}") accessMinutes: Long,
        @Value("\${security.jwt.expiration.refresh}") refreshMinutes: Long,
    ) : this(SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256"), issuer, accessMinutes, refreshMinutes)

    override fun compose(subject: String, roles: List<String>) = IssueToken(
        "Bearer", token(subject, roles, "access", accessExpiration), accessExpiration,
        token(subject, roles, "refresh", refreshExpiration), refreshExpiration,
    )

    override fun compose(refreshToken: String): IssueToken {
        val jwt = decodeRefresh(refreshToken)
        val roles = jwt.getClaimAsString("roles").split(",").map { it.removePrefix("ROLE_") }
        val remaining = requireNotNull(jwt.expiresAt).toEpochMilli() - System.currentTimeMillis()
        return IssueToken("Bearer", token(jwt.subject, roles, "access", accessExpiration),
            accessExpiration, refreshToken, remaining.coerceAtLeast(0))
    }

    override fun decode(token: String): Jwt = decode(token, "access")
    override fun decodeRefresh(token: String): Jwt = decode(token, "refresh")

    override fun customize(configurer: OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer) {
        configurer.decoder(this)
    }

    private fun decode(token: String, expectedUse: String): Jwt {
        val jwt = try {
            decoder.decode(token)
        } catch (exception: JwtValidationException) {
            if (exception.errors.any { it.errorCode == EXPIRED_ERROR_CODE }) throw JwtExpiredException(exception)
            throw exception
        }
        if (jwt.getClaimAsString("token_use") != expectedUse) {
            throw JwtValidationException("Invalid token use", listOf(OAuth2Error("invalid_token")))
        }
        return jwt
    }

    private fun token(subject: String, roles: List<String>, use: String, lifetime: Long): String =
        Jwts.builder().signWith(key).setIssuer(issuer).setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + lifetime)).setSubject(subject)
            .claim("roles", roles.joinToString(",") { "ROLE_$it" }).claim("token_use", use).compact()

    private fun expirationValidator(): OAuth2TokenValidator<Jwt> {
        val timestampValidator = JwtTimestampValidator()
        return OAuth2TokenValidator { jwt ->
            if (timestampValidator.validate(jwt).hasErrors()) OAuth2TokenValidatorResult.failure(OAuth2Error(EXPIRED_ERROR_CODE))
            else OAuth2TokenValidatorResult.success()
        }
    }

    companion object {
        private const val EXPIRED_ERROR_CODE = "token_expired"
    }
}
