package com.nargok.sakemap.config

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nargok.sakemap.data.db.dao.DrinkRecordDao
import com.nargok.sakemap.data.db.entity.DrinkRecordEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(
    entities = [
        DrinkRecordEntity::class,
    ],
    version = 3,
)
@TypeConverters(DateConverter::class, DateTimeConverter::class)
abstract class SakeMapDatabase : RoomDatabase() {
    abstract fun drinkRecordDao(): DrinkRecordDao

    companion object {
        const val DATABASE_NAME = "sake_map_database"
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE drink_record ADD COLUMN manufacturer TEXT")
            }
        }
    }
}

object DateConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    @JvmStatic
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, formatter) }
    }
}

object DateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }
}