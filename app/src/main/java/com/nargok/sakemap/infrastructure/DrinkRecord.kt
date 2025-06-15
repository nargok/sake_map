package com.nargok.sakemap.infrastructure

import com.nargok.sakemap.data.db.dao.DrinkRecordDao
import com.nargok.sakemap.data.db.entity.DrinkRecordEntity
import com.nargok.sakemap.domain.model.DrinkRecord
import com.nargok.sakemap.domain.model.vo.DrinkRecordId
import com.nargok.sakemap.domain.model.vo.Prefecture
import com.nargok.sakemap.domain.repository.DrinkRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrinkRecordRepositoryImpl @Inject constructor(
    private val drinkEffortDao: DrinkRecordDao
) : DrinkRecordRepository {

    override suspend fun search(): List<DrinkRecord> = withContext(Dispatchers.IO) {
        drinkEffortDao.getDrinkRecords().map { it.toModel() }
    }

    override suspend fun find(id: String): DrinkRecord? = withContext(Dispatchers.IO) {
        drinkEffortDao.getDrinkRecord(id).let { it.toModel() }
    }

    override suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        drinkEffortDao.deleteDrinkRecord(id)
    }

    override suspend fun register(drinkRecord: DrinkRecord) = withContext(Dispatchers.IO) {
        drinkEffortDao.insertDrinkRecord(drinkRecord.toEntity())
    }
}

private fun DrinkRecord.toEntity(): DrinkRecordEntity {
    return DrinkRecordEntity(
        id = id.value,
        name = name,
        manufacturer = manufacturer,
        type = type,
        prefecture = prefecture.isoCode,
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
        manufacturer = manufacturer,
        type = type,
        prefecture = Prefecture.fromIsoCode(prefecture),
        rating = rating,
        photoPath = photoPath,
        drinkDate = drinkDate,
        description = description,
        createdAt = createdAt,
    )
}