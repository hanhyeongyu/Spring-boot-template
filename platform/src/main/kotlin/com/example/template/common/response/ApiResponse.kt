package com.example.template.common.response

import com.example.template.common.exception.ErrorCode
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
        fun error(code: ErrorCode, message: String = code.errorMessage) =
            ApiResponse<Unit>(status = code.httpStatus, code = code.errorCode, message = message)
    }
}
