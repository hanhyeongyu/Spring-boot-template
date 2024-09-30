package com.example.template.model


data class Topic(
    val targetType: String,
    val targetId: Long,
){
    fun value(): String{
        return "$targetType:$targetId"
    }
}