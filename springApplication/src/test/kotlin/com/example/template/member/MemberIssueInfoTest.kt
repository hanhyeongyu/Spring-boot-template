package com.example.template.member

import com.example.template.TemplateSpringBootTest
import com.example.template.jwt.JwtComposer
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Duration

@TemplateSpringBootTest
@AutoConfigureMockMvc
class MemberIssueInfoTest {
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var jwtComposer: JwtComposer
    @Autowired private lateinit var objectMapper: ObjectMapper

    @Test fun `should return subject and remaining milliseconds for a valid access token`() {
        val accessToken = jwtComposer.compose("1", listOf("MEMBER")).accessToken

        val body = mockMvc.get("/member/issue/info") { header("Authorization", "Bearer $accessToken") }
            .andExpect { status { isOk() } }
            .andReturn().response.contentAsString
        val data = objectMapper.readTree(body)["data"]

        assertThat(data["id"].asText()).isEqualTo("1")
        assertThat(data["expiresIn"].asLong())
            .isGreaterThan(Duration.ofMinutes(29).toMillis())
            .isLessThanOrEqualTo(Duration.ofMinutes(30).toMillis())
    }

    @Test fun `should respond 401 with 4010 when token is missing`() {
        mockMvc.get("/member/issue/info").andExpect {
            status { isUnauthorized() }
            jsonPath("$.code") { value(4010) }
        }
    }
}
