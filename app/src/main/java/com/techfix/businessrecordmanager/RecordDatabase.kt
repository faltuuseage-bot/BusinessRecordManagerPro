package com.techfix.businessrecordmanager

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "business_records")
data class BusinessRecord(
    @PrimaryKey val id: String,
    val category: String,
    val title: String,
    val details: String,
    val amountPaisa: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface BusinessRecordDao {
    @Query("SELECT * FROM business_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BusinessRecord>>

    @Query("SELECT * FROM business_records WHERE category = :category ORDER BY createdAt DESC")
    fun observeCategory(category: String): Flow<List<BusinessRecord>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: BusinessRecord)

    @Query("DELETE FROM business_records WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Database(entities = [BusinessRecord::class], version = 1, exportSchema = true)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun records(): BusinessRecordDao

    companion object {
        @Volatile private var INSTANCE: RecordDatabase? = null

        fun get(context: Context): RecordDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RecordDatabase::class.java,
                    "business-record-manager.db"
                ).build().also { INSTANCE = it }
            }
    }
}
