package com.nargok.sakemap.infrastructure

import com.nargok.sakemap.data.db.dao.DrinkRecordDao
import com.nargok.sakemap.data.db.entity.DrinkRecordEntity
import com.nargok.sakemap.domain.model.DrinkRecord
import com.nargok.sakemap.domain.model.vo.DrinkRecordId
import com.nargok.sakemap.domain.repository.DrinkRecordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrinkRecordImpl @Inject constructor(
    private val drinkEffortDao: DrinkRecordDao
) : DrinkRecordRepository {
    override fun search(): List<DrinkRecord> {
        return drinkEffortDao.getDrinkRecords().map { it.toModel() }
    }

    override fun find(id: String): DrinkRecord? =
        drinkEffortDao.getDrinkRecord(id).let { it.toModel() }

    override fun delete(id: String) {
        drinkEffortDao.deleteDrinkRecord(id)
    }

    override fun register(drinkRecord: DrinkRecord) {
        drinkEffortDao.insertDrinkRecord(drinkRecord.toEntity())
    }
}

private fun DrinkRecord.toEntity(): DrinkRecordEntity {
    return DrinkRecordEntity(
        id = id.value,
        name = name,
        type = type,
        prefecture = prefecture,
        rating = rating,
        photoPath = photoPath,
        drinkDate = drinkDate,
        description = description,
        createdAt = createdAt
    )
}

private fun DrinkRecordEntity.toModel(): DrinkRecord {
    return DrinkRecord.reconstruct(
        id = DrinkRecordId.reconstruct(id),
        name = name,
        type = type,
        prefecture = prefecture,
        rating = rating,
        photoPath = photoPath,
        drinkDate = drinkDate,
        description = description,
        createdAt = createdAt,
    )
}