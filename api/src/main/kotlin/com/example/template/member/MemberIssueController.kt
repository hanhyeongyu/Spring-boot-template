package com.example.template.member

import com.example.template.projection.UserReadModel
import com.example.template.codable.PasswordEncodable
import com.example.template.common.exception.BaseException
import com.example.template.common.exception.EntityNotFoundException
import com.example.template.common.exception.ErrorCode.INVALID_PASSWORD
import com.example.template.jwt.IssueToken
import com.example.template.jwt.JwtService
import com.example.template.member.MemberIssueController.Companion.ENDPOINT
import com.example.template.model.User
import com.example.template.common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(ENDPOINT)
class MemberIssueController(
    private val jwtService: JwtService,
    private val passwordEncodable: PasswordEncodable,
    private val userReadModel: UserReadModel,
){

    @PostMapping()
    fun issue(@RequestBody request: MemberRequests.IssueTokenRequest): ResponseEntity<ApiResponse<IssueToken>>{
        val member = userReadModel.user(request.email) ?: throw EntityNotFoundException()
        if (isMatch(request, member)){
            val token = jwtService.issueToken(member.id.toString(), listOf("MEMBER"))
            return ResponseEntity.ok(ApiResponse.ok(token))
        }else{
            throw BaseException(INVALID_PASSWORD)
        }
    }


    @PostMapping("/refresh")
    fun issue(@RequestBody request: MemberRequests.RefreshToken): ResponseEntity<ApiResponse<IssueToken>>{
        val token = jwtService.issueToken(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.ok(token))
    }

    private fun isMatch(
        request: MemberRequests.IssueTokenRequest,
        user: User
    ): Boolean{
        return passwordEncodable.matches(request.password, user.encodedPassword.value)
    }


    companion object{
        internal const val ENDPOINT = "member/issue"
    }
}