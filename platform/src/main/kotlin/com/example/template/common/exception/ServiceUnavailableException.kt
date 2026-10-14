package com.example.template.common.exception

class ServiceUnavailableException(message: String) : BaseException(message, ErrorCode.COMMON_SERVICE_UNAVAILABLE)
