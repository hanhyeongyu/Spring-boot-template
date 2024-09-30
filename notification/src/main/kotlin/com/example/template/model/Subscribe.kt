package com.example.template.model

import com.example.template.BaseEntity
import com.example.template.EntityStatus
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import com.example.template.EntityStatus.ACTIVE


@Entity
class Subscribe(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    val targetType: String,

    val targetId: Long,

    val userId: Long,

    var entityStatus: EntityStatus
): BaseEntity(){

    fun topic(): Topic{
        return Topic(targetType, targetId)
    }

    companion object{
        fun create(targetType: String, targetId: Long, userId: Long): Subscribe{
            return Subscribe(
                targetType = targetType,
                targetId = targetId,
                userId = userId,
                entityStatus = ACTIVE
            )
        }
    }
}