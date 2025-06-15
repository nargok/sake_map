package com.nargok.sakemap.domain.repository

import com.nargok.sakemap.domain.model.DrinkRecord

/**
 * お酒記録リポジトリ
 */
interface DrinkRecordRepository {
    suspend fun search(): List<DrinkRecord>

    suspend fun find(id: String): DrinkRecord?

    suspend fun delete(id: String)

    suspend fun register(drinkRecord: DrinkRecord)

}
