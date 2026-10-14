package com.example.template.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader


@Configuration
class FirebaseConfig(
    private val resourceLoader: ResourceLoader,
    @Value("\${firebase.service-account.location:classpath:$DEFAULT_LOCATION}")
    private val serviceAccountLocation: String,
){

    @Bean
    fun initializeFirebase(): FirebaseApp? {
        val serviceAccount = resourceLoader.getResource(serviceAccountLocation).inputStream
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        return FirebaseApp.initializeApp(options)
    }

    companion object{
        private const val DEFAULT_LOCATION = "firebase-service-account.json"
    }
}
