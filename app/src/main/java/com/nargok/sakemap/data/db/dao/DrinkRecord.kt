package com.nargok.sakemap.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nargok.sakemap.data.db.entity.DrinkRecordEntity

@Dao
interface DrinkRecordDao {
    @Query("SELECT * FROM drink_record")
    suspend fun getDrinkRecords(): List<DrinkRecordEntity>

    @Query("SELECT * FROM drink_record WHERE id = :id")
    suspend fun getDrinkRecord(id: String): DrinkRecordEntity

    @Query("DELETE FROM drink_record WHERE id = :id")
    suspend fun deleteDrinkRecord(id: String)

    @Insert
    suspend fun insertDrinkRecord(drinkRecord: DrinkRecordEntity)
}
