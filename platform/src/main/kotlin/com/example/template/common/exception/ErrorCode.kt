package com.example.template.common.exception

enum class ErrorCode(val errorCode: Int, val errorMessage: String, val httpStatus: Int) {
    COMMON_SYSTEM_ERROR(5000, "A temporary error occurred. Please try again later.", 500),
    COMMON_INVALID_PARAMETER(4000, "The requested value is invalid.", 400),
    COMMON_ILLEGAL_STATUS(4001, "Invalid status.", 400),
    COMMON_ENTITY_NOT_FOUND(4040, "Entity not found.", 404),
    COMMON_METHOD_NOT_ALLOWED(4050, "Method not allowed.", 405),
    COMMON_PAYLOAD_TOO_LARGE(4130, "Payload too large.", 413),
    COMMON_UNSUPPORTED_MEDIA_TYPE(4150, "Unsupported media type.", 415),
    COMMON_SERVICE_UNAVAILABLE(5030, "Service unavailable.", 503),
    TOKEN_NOT_FOUND(4010, "Token is required.", 401),
    INVALID_ACCESS_TOKEN(4013, "Invalid access token.", 401),
    INVALID_REFRESH_TOKEN(4014, "Invalid refresh token.", 401),
    INVALID_PASSWORD(4015, "Invalid credentials.", 401),
    ACCESS_DENIED(4030, "Access denied.", 403),
}
