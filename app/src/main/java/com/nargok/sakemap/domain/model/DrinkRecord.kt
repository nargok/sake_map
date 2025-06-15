package com.nargok.sakemap.domain.model

import com.nargok.sakemap.domain.model.vo.DrinkRecordId
import com.nargok.sakemap.domain.model.vo.DrinkType
import com.nargok.sakemap.domain.model.vo.Prefecture
import java.time.LocalDate
import java.time.LocalDateTime

data class DrinkRecord private constructor(
    val id: DrinkRecordId,
    val name: String,                    // 銘柄名
    val manufacturer: String?,           // 製造元
    val type: DrinkType,                // お酒の種類
    val prefecture: Prefecture,             // 都道府県
    val rating: Int,                    // 評価（1-5）
    val photoPath: String?,             // 写真パス
    val drinkDate: LocalDate,           // 飲んだ日付
    val description: String?,           // 説明
    val createdAt: LocalDateTime = LocalDateTime.now()

) {

    companion object {
        fun create(
            name: String,
            manufacturer: String?,
            type: DrinkType,
            prefecture: Prefecture,
            rating: Int,
            photoPath: String?,
            description: String?
        ): DrinkRecord {
            return DrinkRecord(
                id = DrinkRecordId.create(),
                name = name,
                manufacturer = manufacturer,
                type = type,
                prefecture = prefecture,
                rating = rating,
                photoPath = photoPath,
                drinkDate = LocalDate.now(),
                description = description
            )
        }

        fun reconstruct(
            id: DrinkRecordId,
            name: String,
            manufacturer: String?,
            type: DrinkType,
            prefecture: Prefecture,
            rating: Int,
            photoPath: String?,
            drinkDate: LocalDate,
            description: String?,
            createdAt: LocalDateTime,
        ): DrinkRecord {
            return DrinkRecord(
                id = id,
                name = name,
                manufacturer = manufacturer,
                type = type,
                prefecture = prefecture,
                rating = rating,
                photoPath = photoPath,
                drinkDate = drinkDate,
                description = description,
                createdAt = createdAt,
            )
        }
    }

}
