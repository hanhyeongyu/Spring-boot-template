package com.example.template.repository

import com.example.template.model.Subscribe
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SubscribeRepository: JpaRepository<Subscribe, Long>{
    fun findByUserId(userId : Long) : List<Subscribe>
    fun findByUserIdAndTargetTypeAndTargetId(userId: Long, targetType: String, targetId: Long): Optional<Subscribe>
}