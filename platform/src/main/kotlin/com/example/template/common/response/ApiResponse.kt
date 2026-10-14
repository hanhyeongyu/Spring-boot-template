package com.example.template.common.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val status: Int,
    val code: Int? = null,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> ok(data: T) = ApiResponse(status = 200, message = "OK", data = data)
        fun ok() = ApiResponse<Unit>(status = 200, message = "OK")
        fun error(status: Int, code: Int, message: String) = ApiResponse<Unit>(status = status, code = code, message = message)
    }
}
