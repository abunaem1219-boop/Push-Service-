package com.taqwanet.app.feature.reels.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.domain.model.ReelComment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<ReelComment>,
    onSendComment: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EmeraldSurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .fillMaxHeight(0.75f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মন্তব্যসমূহ (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = DarkTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // কমেন্ট লিস্ট
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(comments) { comment ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MintContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = comment.userName.take(1),
                                color = MintBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = comment.userName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AntiqueGold
                            )
                            Text(
                                text = comment.commentText,
                                fontSize = 14.sp,
                                color = DarkTextPrimary
                            )
                        }
                    }
                }
            }

            // কমেন্ট ইনপুট বক্স
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("একটি সুন্দর মন্তব্য লিখুন...", color = DarkTextMuted, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendComment(textInput)
                            textInput = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "পাঠান", tint = MintBright)
                }
            }
        }
    }
}

@Composable
fun UploadReelDialog(
    isUploading: Boolean,
    onUploadConfirmed: (Uri, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var captionText by remember { mutableStateOf("") }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedVideoUri = uri
    }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        containerColor = EmeraldSurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "ইসলামিক রিলস আপলোড",
                fontWeight = FontWeight.Bold,
                color = AntiqueGold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "আপনার গ্যালারি থেকে সংক্ষিপ্ত নসিহত বা তিলাওয়াত ভিডিও নির্বাচন করুন।",
                    fontSize = 13.sp,
                    color = DarkTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { videoPickerLauncher.launch("video/*") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = MintBright)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedVideoUri != null) "ভিডিও নির্বাচিত হয়েছে ✓" else "গ্যালারি থেকে ভিডিও নিন",
                        color = DarkTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    placeholder = { Text("ভিডিওর শিরোনাম বা হাদিস রেফারেন্স লিখুন...", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                if (isUploading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MintBright
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ক্লাউডিনারিতে অপ্টিমাইজ ও আপলোড হচ্ছে...",
                        fontSize = 12.sp,
                        color = MintSoft
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedVideoUri?.let { uri ->
                        onUploadConfirmed(uri, captionText)
                    }
                },
                enabled = selectedVideoUri != null && !isUploading,
                colors = ButtonDefaults.buttonColors(containerColor = MintBright)
            ) {
                Text("আপলোড করুন", color = EmeraldDeepDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (!isUploading) {
                TextButton(onClick = onDismiss) {
                    Text("বাতিল", color = DarkTextSecondary)
                }
            }
        }
    )
}