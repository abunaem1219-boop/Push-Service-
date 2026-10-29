package com.taqwanet.app.domain.model

data class ReelItem(
    val id: String = "",
    val videoUrl: String = "",
    val title: String = "",
    val uploaderId: String = "",
    val uploaderName: String = "মুসাফির",
    val uploaderPhoto: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class ReelComment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhoto: String = "",
    val commentText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

object CloudinaryConfig {
    // আপনার ক্লাউডিনারি Cloud Name এবং Unsigned Upload Preset এখানে বসান
    // (ডিফল্ট প্রস্তুতকৃত টেস্ট ক্রেডেনশিয়াল দেওয়া হলো যা সরাসরি কাজ করবে)
    const val CLOUD_NAME = "bco4qfcw" 
    const val UPLOAD_PRESET = "taqwa_net"
}