package com.example.template.jwt

import java.security.Principal

interface JwtService {
    fun issueToken(id: String, roles: List<String>): IssueToken
    fun issueToken(refreshToken: String): IssueToken
    fun authentication(token: String): AuthenticationToken
    fun accessTokenInfo(principal: Principal): AccessTokenInfo
}