package com.nargok.sakemap.domain.repository

import com.nargok.sakemap.domain.model.DrinkRecord

/**
 * お酒記録リポジトリ
 */
interface DrinkRecordRepository {
    fun search(): List<DrinkRecord>

    fun find(id: String): DrinkRecord?

    fun delete(id: String)

    fun register(drinkRecord: DrinkRecord)

}
