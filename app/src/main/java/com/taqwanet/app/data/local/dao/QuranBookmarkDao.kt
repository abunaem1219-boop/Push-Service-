package com.taqwanet.app.data.local.dao

import com.taqwanet.app.data.local.entity.QuranBookmarkEntity
import kotlinx.coroutines.flow.Flow

interface QuranBookmarkDao {
    fun getAllBookmarks(): Flow<List<QuranBookmarkEntity>>
    suspend fun insertBookmark(bookmark: QuranBookmarkEntity)
    suspend fun deleteBookmark(bookmark: QuranBookmarkEntity)
    suspend fun removeSpecificBookmark(surah: Int, ayah: Int)
    suspend fun isBookmarked(surah: Int, ayah: Int): Boolean
}