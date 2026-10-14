package com.example.template.jwt

import com.example.template.common.exception.BaseException
import com.example.template.common.exception.ErrorCode.INVALID_REFRESH_TOKEN
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service


@Service
internal class JwtServiceImp(
    private val jwtComposer: JwtComposer,
    private val jwtDecoder: JwtTokenDecoder,
    private val tokenRepository: JwtRepository,
): JwtService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun issueToken(id: String, roles: List<String>): IssueToken {
        val compose =  jwtComposer.compose(id, roles)
        tokenRepository.add(id, compose.refreshToken, compose.refreshExpiresIn)
        return compose
    }


    override fun issueToken(refreshToken: String): IssueToken {
        try{
            val jwt = jwtDecoder.decodeRefresh(refreshToken)
            val id = jwt.subject

            if (validateToken(id, refreshToken)){
                return jwtComposer.compose(refreshToken)
            }else{
                throw BaseException(INVALID_REFRESH_TOKEN)
            }
        }catch (e: JwtException){
            log.error("Issue token ", e)
            throw BaseException(INVALID_REFRESH_TOKEN)
        }
    }


    override fun authentication(token: String): AuthenticationToken {
        val jwtAuthenticationToken = JwtAuthenticationToken(jwtDecoder.decode(token))
        return AuthenticationToken(jwtAuthenticationToken)
    }

    private fun validateToken(id: String, refreshToken: String): Boolean{
        return tokenRepository.token(id) == refreshToken
    }


}