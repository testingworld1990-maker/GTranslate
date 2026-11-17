package com.tanglishvideotranslator.cache

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.ColumnInfo

@Entity(tableName = "subtitle_cache")
data class SubtitleCacheEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "video_hash") val videoHash: String,
    @ColumnInfo(name = "chunk_index") val chunkIndex: Long,
    @ColumnInfo(name = "style") val style: String,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "payload_json") val payloadJson: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)

@Dao
interface SubtitleCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SubtitleCacheEntry): Long

    @Query("SELECT payload_json FROM subtitle_cache WHERE video_hash = :videoHash AND chunk_index = :chunkIndex AND style = :style AND model = :model LIMIT 1")
    suspend fun getCachedPayload(videoHash: String, chunkIndex: Long, style: String, model: String): String?

    @Query("DELETE FROM subtitle_cache WHERE video_hash = :videoHash")
    suspend fun clearForVideo(videoHash: String)
}

@Database(entities = [SubtitleCacheEntry::class], version = 1, exportSchema = false)
abstract class SubtitleCacheDatabase : RoomDatabase() {
    abstract fun subtitleCacheDao(): SubtitleCacheDao

    companion object {
        @Volatile
        private var INSTANCE: SubtitleCacheDatabase? = null

        fun getInstance(context: Context): SubtitleCacheDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SubtitleCacheDatabase::class.java,
                    "subtitle_cache.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
