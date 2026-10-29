package com.taqwanet.app.data.remote

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.taqwanet.app.domain.model.AppUpdateInfo
import kotlinx.coroutines.tasks.await

class RemoteConfigManager(private val context: Context) {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val TAG = "TaqwaUpdate"

    suspend fun checkForUpdates(): AppUpdateInfo {
        return try {
            // ১. অ্যাপের বর্তমান ভার্সন কোড সংগ্রহ
            val currentVersionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0)).longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            }

            Log.d(TAG, "Current Installed App VersionCode: $currentVersionCode")

            // ২. ফায়ারবেস থেকে ডকুমেন্টের ডেটা সংগ্রহ
            val snapshot = firestore.collection("app_config")
                .document("version_control")
                .get()
                .await()

            if (!snapshot.exists()) {
                Log.w(TAG, "Firestore document 'app_config/version_control' does not exist yet.")
                return AppUpdateInfo()
            }

            val data = snapshot.data
            Log.d(TAG, "Firestore Data Received: $data")

            // ৩. টাইপ-সেফ পার্সিং (ফায়ারবেসে ডেটা নম্বর বা স্ট্রিং যেকোনোটিতেই থাকুক)
            val latestCode = parseInteger(data?.get("latest_version_code")) ?: currentVersionCode
            val minRequiredCode = parseInteger(data?.get("min_version_code")) ?: currentVersionCode
            val latestName = data?.get("latest_version_name")?.toString() ?: "1.1.0"
            val title = data?.get("title_bn")?.toString() ?: "নতুন সংস্করণ এসেছে!"
            val url = data?.get("download_url")?.toString() ?: "https://play.google.com/store"

            // রিলিজ নোটস লিস্ট তৈরি
            val notesList = when (val notes = data?.get("release_notes")) {
                is List<*> -> notes.map { it.toString() }
                is String -> notes.split(",").map { it.trim() }
                else -> listOf("নতুন ইসলামিক ফিচার যুক্ত করা হয়েছে।", "বাগ ফিক্স ও কর্মক্ষমতা উন্নত করা হয়েছে।")
            }

            // আপডেট প্রয়োজন কিনা যাচাই
            val isUpdateNeeded = currentVersionCode < latestCode
            val isForce = currentVersionCode < minRequiredCode

            Log.i(TAG, "Comparison: Current($currentVersionCode) < Latest($latestCode) -> isUpdateNeeded: $isUpdateNeeded, isForce: $isForce")

            AppUpdateInfo(
                isUpdateAvailable = isUpdateNeeded,
                isForceUpdate = isForce,
                latestVersionName = latestName,
                latestVersionCode = latestCode,
                titleBn = title,
                releaseNotesBn = notesList,
                downloadUrl = url
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching update info from Firebase: ${e.message}", e)
            AppUpdateInfo()
        }
    }

    private fun parseInteger(value: Any?): Int? {
        return when (value) {
            is Number -> value.toInt()
            is String -> value.trim().toIntOrNull()
            else -> null
        }
    }
}