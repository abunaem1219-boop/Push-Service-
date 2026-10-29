package com.taqwanet.app.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.taqwanet.app.domain.model.CloudinaryConfig
import com.taqwanet.app.domain.model.ReelComment
import com.taqwanet.app.domain.model.ReelItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class ReelsRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ১২০ সেকেন্ডের দীর্ঘ টাইমআউট নিশ্চিত করা হলো যাতে বড় ভিডিও আপলোডে ফেইল না করে
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    fun getReelsStream(): Flow<List<ReelItem>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: ""

        val listener = firestore.collection("reels")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList()) // এরর হলেও ক্র্যাশ করবে না
                    return@addSnapshotListener
                }

                val reels = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val url = doc.getString("videoUrl") ?: ""
                    // ভিডিও লিংক না থাকলে সেটিকে ফিল্টার আউট করা
                    if (url.isBlank()) return@mapNotNull null

                    val title = doc.getString("title") ?: "ইসলামিক নসিহত"
                    val uId = doc.getString("uploaderId") ?: ""
                    val uName = doc.getString("uploaderName") ?: "মুসাফির"
                    val uPhoto = doc.getString("uploaderPhoto") ?: ""
                    val likes = doc.getLong("likesCount")?.toInt() ?: 0
                    val comments = doc.getLong("commentsCount")?.toInt() ?: 0
                    val likesList = doc.get("likedUsers") as? List<*> ?: emptyList<Any>()
                    val isLiked = likesList.contains(currentUserId)

                    ReelItem(id, url, title, uId, uName, uPhoto, likes, comments, isLiked)
                } ?: emptyList()

                trySend(reels)
            }

        awaitClose { listener.remove() }
    }

    suspend fun uploadVideoToCloudinary(videoUri: Uri, title: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val tempFile = File(context.cacheDir, "upload_reel_${System.currentTimeMillis()}.mp4")
            context.contentResolver.openInputStream(videoUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (!tempFile.exists() || tempFile.length() == 0L) {
                return@withContext Result.failure(Exception("ভিডিও ফাইলটি পড়া সম্ভব হয়নি।"))
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", CloudinaryConfig.UPLOAD_PRESET)
                .addFormDataPart(
                    "file",
                    tempFile.name,
                    tempFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/${CloudinaryConfig.CLOUD_NAME}/video/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            tempFile.delete() // ক্যাশ পরিষ্কার

            if (response.isSuccessful) {
                val json = JSONObject(responseString)
                val secureUrl = json.getString("secure_url")

                // সরাসরি প্লে-যোগ্য আসল ভিডিও লিঙ্ক সংরক্ষণ
                saveReelToFirestore(secureUrl, if (title.isBlank()) "ইসলামিক নসিহত" else title)
                Result.success(secureUrl)
            } else {
                Result.failure(Exception("Cloudinary Error ($responseString)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveReelToFirestore(videoUrl: String, title: String) {
        val user = auth.currentUser
        val data = hashMapOf(
            "videoUrl" to videoUrl,
            "title" to title,
            "uploaderId" to (user?.uid ?: ""),
            "uploaderName" to (user?.displayName ?: "ইসলামিক গবেষক"),
            "uploaderPhoto" to (user?.photoUrl?.toString() ?: ""),
            "likesCount" to 0,
            "commentsCount" to 0,
            "likedUsers" to listOf<String>(),
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("reels").add(data).await()
    }

    suspend fun toggleLike(reel: ReelItem): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val docRef = firestore.collection("reels").document(reel.id)

        return try {
            if (reel.isLikedByMe) {
                docRef.update(
                    "likedUsers", FieldValue.arrayRemove(userId),
                    "likesCount", FieldValue.increment(-1)
                ).await()
                false
            } else {
                docRef.update(
                    "likedUsers", FieldValue.arrayUnion(userId),
                    "likesCount", FieldValue.increment(1)
                ).await()
                true
            }
        } catch (e: Exception) {
            reel.isLikedByMe
        }
    }

    suspend fun postComment(reelId: String, text: String): Boolean {
        val user = auth.currentUser ?: return false
        val commentData = hashMapOf(
            "userId" to user.uid,
            "userName" to (user.displayName ?: "ইউজার"),
            "userPhoto" to (user?.photoUrl?.toString() ?: ""),
            "commentText" to text,
            "timestamp" to System.currentTimeMillis()
        )

        return try {
            firestore.collection("reels").document(reelId)
                .collection("comments").add(commentData).await()
            firestore.collection("reels").document(reelId)
                .update("commentsCount", FieldValue.increment(1)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCommentsStream(reelId: String): Flow<List<ReelComment>> = callbackFlow {
        val listener = firestore.collection("reels").document(reelId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    ReelComment(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        userName = doc.getString("userName") ?: "ইউজার",
                        userPhoto = doc.getString("userPhoto") ?: "",
                        commentText = doc.getString("commentText") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}