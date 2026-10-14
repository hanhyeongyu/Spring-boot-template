package com.example.template.model

import com.example.template.common.exception.InvalidParamException

@JvmInline
value class Email(val value: String){

    init {
        if (!isValidEmail()){
            throw InvalidParamException("Email is not valid")
        }
    }

    private fun isValidEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
        return value.matches(emailRegex)
    }
}