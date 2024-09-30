package com.example.template.firebase

import com.google.firebase.messaging.SendResponse


fun SendResponse.invalidToken(): Boolean{
    if (this.isSuccessful){
        return false
    }

    val statusCode = this.exception.httpResponse.statusCode
    //UNREGISTERED(HTTP 404), INVALID_ARGUMENT(HTTP 400)
    return statusCode == 404 || statusCode == 400
}
