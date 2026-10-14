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

Manages user tokens, token expiration, subscriptions, and notification workflows. Depends on `support:notification` for delivery.   
  

### support:notification

Owns Firebase configuration, FCM message delivery, and topic subscription transport. It has no dependency on user or notification domain modules. Callers use string tokens/topics and SDK-independent delivery results. The local credential file is `support/notification/src/main/resources/firebase-service-account.json`; it is excluded from the jar.

## Common configuration

The Gradle group is `projectGroup`, independent of the Kotlin compiler version and source package. JPA `Entity`, `MappedSuperclass`, and `Embeddable` classes use the Jakarta all-open configuration in every module. `BaseEntity` audit timestamps use `Instant`.

`ApiResponse` contains `status`, `code`, `message`, and optional `data`. Each common error declares its HTTP status explicitly. Internal errors and rejected input values are not exposed in error responses. Application exceptions and Spring Security failures use the same error envelope; existing file/stream endpoints keep their response types.

Private DB settings belong in ignored `support/storage/src/main/resources/database-keys.yml`, loaded from the repository root. `database-keys.example.yml` documents the names. Private key files are excluded from packaged resources. JWT and password encoder secrets are environment variables.

## Optional services

| Feature | Selection | Required configuration |
|---|---|---|
| JWT authentication | Always on | `JWT_SECRET` (HS256 key, at least 32 bytes), `PASSWORD_ENCODER_SECRET`, Redis cluster |
| Local files (default) | `FILE_STORAGE_TYPE=local` or omitted | Existing local file paths |
| S3-compatible files | `FILE_STORAGE_TYPE=s3` | `S3_ENDPOINT`, `S3_BUCKET`, `S3_REGION`, `S3_ACCESS_KEY`, `S3_SECRET_KEY` |
| SMTP email | `MAIL_ENABLED=true` | `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`; sender identity is supplied to `EmailSender` |
| Naver SENS SMS | `SMS_ENABLED=true` | `SENS_ACCESS_KEY`, `SENS_SECRET_KEY`, `SENS_SERVICE_ID`, `SENS_FROM` |
| Firebase notifications | `NOTIFICATION_ENABLED=true` | Ignored service account file at `support/notification/src/main/resources/firebase-service-account.json` |

S3, email, SMS, and FCM are disabled unless selected. The SENS adapter retains Korean mobile-number support (`+82` or domestic `01…`); it does not claim international delivery support. S3 keys use the supplied path as-is.

`POST /member/issue` takes an email/password request, and `POST /member/issue/refresh` takes `{ "refreshToken": "…" }`. Both return tokens in `ApiResponse.data`, with expiration fields in milliseconds.

JWT access and refresh tokens now carry a `token_use` claim. Old tokens without this claim are rejected and require a new login. Both configured JWT lifetimes are interpreted in minutes; refresh responses report the remaining refresh lifetime.

## Verification

Run `./gradlew build`. The existing context test uses isolated H2 memory storage and disabled external senders; it does not access the development database. JWT behavior tests cover token purpose, lifetime conversion, and role preservation. External S3/SMTP/SENS/FCM calls require separate integration verification with project settings.

The prior notification subscription persistence issue remains outside this change: `SubscribeService.subscribe` still needs an explicit consistency design for saving subscriptions and handling FCM failures.
