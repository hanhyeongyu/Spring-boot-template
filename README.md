# SpringBoot Template


## Modules
### springApplication

`Main` entry point for the application.

***

### api

Handles web requests and responses.

***  

### application

Contains the facade layer, orchestrating the interaction between various components and services.

***  

### security

Implements Spring Security using `JWT` for authentication and authorization.

***

### platform

Contains platform-level code for application-wide utilities and configurations.

***

### support

Provides various supportive functionalities to the application.

##### support:log

Manages logging using `logback`

##### support:storage

Integrates `MySQL` with the application using `Spring Data JPA` for data persistence.

##### support:file

Manages the storage and handling of files such as images, videos, and other media. Provides utilities for uploading, storing, and retrieving files.

***

### models

Represents the core business logic of the application.

##### user

Manages the `business logic` related to user operations.

##### notification

Manages tokens and subscriptions to send notifications to users. Utilizes `FCM (Firebase Cloud Messaging)` to deliver alerts and messages efficiently.   
  
