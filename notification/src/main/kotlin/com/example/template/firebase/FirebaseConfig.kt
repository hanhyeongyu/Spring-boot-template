package com.example.template.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream


@Configuration
class FirebaseConfig{

    @Bean
    fun initializeFirebase(): FirebaseApp? {
        val serviceAccount = FileInputStream(FILE_LOCATION)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        return FirebaseApp.initializeApp(options)
    }

    companion object{
        private const val FILE_LOCATION = "notification/src/main/resources/firebase-service-account.json"
    }
}