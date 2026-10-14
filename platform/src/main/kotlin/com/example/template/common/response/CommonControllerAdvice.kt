package com.example.template.common.response

import com.example.template.common.exception.BaseException
import com.example.template.common.exception.ErrorCode
import org.apache.catalina.connector.ClientAbortException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
internal class CommonControllerAdvice {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BaseException::class)
    fun business(exception: BaseException): ResponseEntity<ApiResponse<Unit>> {
        val code = exception.errorCode ?: ErrorCode.COMMON_SYSTEM_ERROR
        if (code.httpStatus.is5xxServerError) log.error("Application failure", exception)
        return response(code, if (code.httpStatus.is5xxServerError) code.errorMessage else exception.message ?: code.errorMessage)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class, MissingServletRequestParameterException::class,
        MissingServletRequestPartException::class, MethodArgumentTypeMismatchException::class,
        MethodArgumentNotValidException::class)
    fun invalidRequest() = response(ErrorCode.COMMON_INVALID_PARAMETER)

    @ExceptionHandler(NoHandlerFoundException::class, NoResourceFoundException::class)
    fun notFound() = response(ErrorCode.COMMON_ENTITY_NOT_FOUND)

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotAllowed() = response(ErrorCode.COMMON_METHOD_NOT_ALLOWED)

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun unsupportedMediaType() = response(ErrorCode.COMMON_UNSUPPORTED_MEDIA_TYPE)

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun uploadTooLarge() = response(ErrorCode.COMMON_PAYLOAD_TOO_LARGE)

    @ExceptionHandler(ClientAbortException::class)
    fun clientDisconnected() { log.debug("Client disconnected before response completed") }

    @ExceptionHandler(Exception::class)
    fun unexpected(exception: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("Unexpected request failure", exception)
        return response(ErrorCode.COMMON_SYSTEM_ERROR)
    }

    private fun response(code: ErrorCode, message: String = code.errorMessage): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(code.httpStatus).body(ApiResponse.error(code.httpStatus.value(), code.errorCode, message))
}
