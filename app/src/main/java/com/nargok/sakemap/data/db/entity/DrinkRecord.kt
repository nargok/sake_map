package com.nargok.sakemap.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nargok.sakemap.domain.model.vo.DrinkType
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "drink_record",
    indices = [
        Index(value = ["prefecture"])
    ]
)
data class DrinkRecordEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: DrinkType,
    val prefecture: String,
    val rating: Int,
    val photoPath: String?,
    val drinkDate: LocalDate,
    val description: String?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)