package com.nargok.sakemap.config

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.nargok.sakemap.BuildConfig
import com.nargok.sakemap.data.db.dao.DrinkRecordDao

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): SakeMapDatabase {
        val builder = Room.databaseBuilder(
            app,
            SakeMapDatabase::class.java,
            SakeMapDatabase.DATABASE_NAME
        )

        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration(false)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideDrinkRecordDao(db: SakeMapDatabase): DrinkRecordDao {
        return db.drinkRecordDao()
    }

}
