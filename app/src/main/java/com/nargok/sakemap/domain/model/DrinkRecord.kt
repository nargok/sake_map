package com.nargok.sakemap.domain.model

import com.nargok.sakemap.domain.model.vo.DrinkType
import java.time.LocalDate
import java.time.LocalDateTime

data class DrinkRecord(
    val id: String,
    val name: String,                    // 銘柄名
    val type: DrinkType,                // お酒の種類
    val prefecture: String,             // 都道府県
    val rating: Int,                    // 評価（1-5）
    val photoPath: String?,             // 写真パス
    val drinkDate: LocalDate,           // 飲んだ日付
    val createdAt: LocalDateTime = LocalDateTime.now()
)