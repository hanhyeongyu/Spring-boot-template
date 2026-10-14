package com.example.template

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant


@MappedSuperclass
abstract class BaseEntity {
    @CreationTimestamp
    @Column(updatable = false)
    open val createdAt: Instant? = null

    @UpdateTimestamp
    @Column
    open var updatedAt: Instant? = null
        protected set
}