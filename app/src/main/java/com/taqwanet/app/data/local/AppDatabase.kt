package com.taqwanet.app.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.taqwanet.app.data.local.dao.PrayerRecordDao
import com.taqwanet.app.data.local.dao.QuranBookmarkDao
import com.taqwanet.app.data.local.entity.PrayerRecordEntity
import com.taqwanet.app.data.local.entity.QuranBookmarkEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val prayerRecordsTrigger = MutableStateFlow(System.currentTimeMillis())
    private val quranBookmarksTrigger = MutableStateFlow(System.currentTimeMillis())

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS prayer_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dateString TEXT NOT NULL,
                prayerId TEXT NOT NULL,
                isCompleted INTEGER NOT NULL,
                completedAt INTEGER,
                isDelayed INTEGER NOT NULL DEFAULT 0,
                UNIQUE(dateString, prayerId)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS quran_bookmarks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                surahNumber INTEGER NOT NULL,
                ayahNumber INTEGER NOT NULL,
                surahNameBn TEXT NOT NULL,
                surahNameAr TEXT NOT NULL,
                savedAtTimestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    private val prayerRecordDaoInstance = object : PrayerRecordDao {
        override fun getRecordsForDate(date: String): Flow<List<PrayerRecordEntity>> {
            // ট্রিগার ফ্লো এর সাথে ম্যাপ করে রিয়েল-টাইম রিফ্রেশ নিশ্চিত করা হলো
            return prayerRecordsTrigger.map {
                val list = mutableListOf<PrayerRecordEntity>()
                val db = readableDatabase
                val cursor = db.rawQuery(
                    "SELECT id, dateString, prayerId, isCompleted, completedAt, isDelayed FROM prayer_records WHERE dateString = ?",
                    arrayOf(date)
                )
                cursor.use {
                    while (it.moveToNext()) {
                        list.add(
                            PrayerRecordEntity(
                                id = it.getLong(0),
                                dateString = it.getString(1),
                                prayerId = it.getString(2),
                                isCompleted = it.getInt(3) == 1,
                                completedAt = if (it.isNull(4)) null else it.getLong(4),
                                isDelayed = it.getInt(5) == 1
                            )
                        )
                    }
                }
                list
            }
        }

        override suspend fun insertOrUpdateRecord(record: PrayerRecordEntity) = withContext(Dispatchers.IO) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("dateString", record.dateString)
                put("prayerId", record.prayerId)
                put("isCompleted", if (record.isCompleted) 1 else 0)
                put("completedAt", record.completedAt)
                put("isDelayed", if (record.isDelayed) 1 else 0)
            }
            db.insertWithOnConflict("prayer_records", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            // পরিবর্তন হওয়া মাত্রই UI-কে নোটিফাই করা
            prayerRecordsTrigger.value = System.currentTimeMillis()
        }

        override fun getRecordsBetweenDates(startDate: String, endDate: String): Flow<List<PrayerRecordEntity>> {
            return prayerRecordsTrigger.map {
                val list = mutableListOf<PrayerRecordEntity>()
                val db = readableDatabase
                val cursor = db.rawQuery(
                    "SELECT id, dateString, prayerId, isCompleted, completedAt, isDelayed FROM prayer_records WHERE dateString BETWEEN ? AND ? ORDER BY dateString DESC",
                    arrayOf(startDate, endDate)
                )
                cursor.use {
                    while (it.moveToNext()) {
                        list.add(
                            PrayerRecordEntity(
                                id = it.getLong(0),
                                dateString = it.getString(1),
                                prayerId = it.getString(2),
                                isCompleted = it.getInt(3) == 1,
                                completedAt = if (it.isNull(4)) null else it.getLong(4),
                                isDelayed = it.getInt(5) == 1
                            )
                        )
                    }
                }
                list
            }
        }

        override fun getTotalCompletedPrayersCount(): Flow<Int> {
            val flow = MutableStateFlow(0)
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM prayer_records WHERE isCompleted = 1", null)
            cursor.use {
                if (it.moveToFirst()) {
                    flow.value = it.getInt(0)
                }
            }
            return flow.asStateFlow()
        }
    }

    private val quranBookmarkDaoInstance = object : QuranBookmarkDao {
        override fun getAllBookmarks(): Flow<List<QuranBookmarkEntity>> {
            return quranBookmarksTrigger.map {
                val list = mutableListOf<QuranBookmarkEntity>()
                val db = readableDatabase
                val cursor = db.rawQuery("SELECT id, surahNumber, ayahNumber, surahNameBn, surahNameAr, savedAtTimestamp FROM quran_bookmarks ORDER BY savedAtTimestamp DESC", null)
                cursor.use {
                    while (it.moveToNext()) {
                        list.add(
                            QuranBookmarkEntity(
                                id = it.getLong(0),
                                surahNumber = it.getInt(1),
                                ayahNumber = it.getInt(2),
                                surahNameBn = it.getString(3),
                                surahNameAr = it.getString(4),
                                savedAtTimestamp = it.getLong(5)
                            )
                        )
                    }
                }
                list
            }
        }

        override suspend fun insertBookmark(bookmark: QuranBookmarkEntity) = withContext(Dispatchers.IO) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("surahNumber", bookmark.surahNumber)
                put("ayahNumber", bookmark.ayahNumber)
                put("surahNameBn", bookmark.surahNameBn)
                put("surahNameAr", bookmark.surahNameAr)
                put("savedAtTimestamp", bookmark.savedAtTimestamp)
            }
            db.insertWithOnConflict("quran_bookmarks", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            quranBookmarksTrigger.value = System.currentTimeMillis()
        }

        override suspend fun deleteBookmark(bookmark: QuranBookmarkEntity) = withContext(Dispatchers.IO) {
            val db = writableDatabase
            db.delete("quran_bookmarks", "id = ?", arrayOf(bookmark.id.toString()))
            quranBookmarksTrigger.value = System.currentTimeMillis()
        }

        override suspend fun removeSpecificBookmark(surah: Int, ayah: Int) = withContext(Dispatchers.IO) {
            val db = writableDatabase
            db.delete("quran_bookmarks", "surahNumber = ? AND ayahNumber = ?", arrayOf(surah.toString(), ayah.toString()))
            quranBookmarksTrigger.value = System.currentTimeMillis()
        }

        override suspend fun isBookmarked(surah: Int, ayah: Int): Boolean = withContext(Dispatchers.IO) {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT 1 FROM quran_bookmarks WHERE surahNumber = ? AND ayahNumber = ? LIMIT 1", arrayOf(surah.toString(), ayah.toString()))
            cursor.use { it.moveToFirst() }
        }
    }

    fun prayerRecordDao(): PrayerRecordDao = prayerRecordDaoInstance
    fun quranBookmarkDao(): QuranBookmarkDao = quranBookmarkDaoInstance

    companion object {
        private const val DATABASE_NAME = "taqwanet.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}