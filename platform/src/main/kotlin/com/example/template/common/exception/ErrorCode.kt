package com.example.template.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val errorCode: Int, val errorMessage: String, val httpStatus: HttpStatus) {
    COMMON_SYSTEM_ERROR(5000, "A temporary error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
    COMMON_INVALID_PARAMETER(4000, "The requested value is invalid.", HttpStatus.BAD_REQUEST),
    COMMON_ILLEGAL_STATUS(4001, "Invalid status.", HttpStatus.BAD_REQUEST),
    COMMON_ENTITY_NOT_FOUND(4040, "Entity not found.", HttpStatus.NOT_FOUND),
    COMMON_METHOD_NOT_ALLOWED(4050, "Method not allowed.", HttpStatus.METHOD_NOT_ALLOWED),
    COMMON_DUPLICATE_REQUEST(4090, "Duplicate request.", HttpStatus.CONFLICT),
    COMMON_PAYLOAD_TOO_LARGE(4130, "Payload too large.", HttpStatus.PAYLOAD_TOO_LARGE),
    COMMON_UNSUPPORTED_MEDIA_TYPE(4150, "Unsupported media type.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    COMMON_SERVICE_UNAVAILABLE(5030, "Service unavailable.", HttpStatus.SERVICE_UNAVAILABLE),
    TOKEN_NOT_FOUND(4010, "Token is required.", HttpStatus.UNAUTHORIZED),
    EXPIRED_ACCESS_TOKEN(4011, "Access token expired.", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN(4012, "Refresh token expired.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN(4013, "Invalid access token.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(4014, "Invalid refresh token.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD(4015, "Invalid credentials.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(4030, "Access denied.", HttpStatus.FORBIDDEN),
}
