package com.taqwanet.app.feature.home.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.domain.model.AppUpdateInfo

@Composable
fun VersionUpdateDialog(
    updateInfo: AppUpdateInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = {
            if (!updateInfo.isForceUpdate) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = !updateInfo.isForceUpdate,
            dismissOnClickOutside = !updateInfo.isForceUpdate
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = AntiqueGold),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
            border = BorderStroke(
                1.5.dp,
                Brush.verticalGradient(listOf(AntiqueGold, MintBright, EmeraldCardDark))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF093327), Color(0xFF041C15))
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // রকেট / আপডেট আইকন গ্লো
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(AntiqueGold.copy(alpha = 0.3f), Color.Transparent))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ভার্সন ব্যাজ
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MintContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MintBright.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "নতুন সংস্করণ v${updateInfo.latestVersionName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MintSoft,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = updateInfo.titleBn,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // নতুন ফিচারের বুলেট পয়েন্ট লিস্ট
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmeraldDeepDark.copy(alpha = 0.6f))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "নতুন কী কী যুক্ত হয়েছে:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    updateInfo.releaseNotesBn.forEach { note ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MintBright,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = note,
                                fontSize = 13.sp,
                                color = DarkTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // আপডেট করুন বোতাম
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.downloadUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MintBright)
                ) {
                    Text(
                        text = "এখনই আপডেট করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EmeraldDeepDark
                    )
                }

                // যদি অপশনাল আপডেট হয়, তবে "পরে করুন" বোতাম থাকবে
                if (!updateInfo.isForceUpdate) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "পরে মনে করিয়ে দিন",
                            fontSize = 13.sp,
                            color = DarkTextSecondary
                        )
                    }
                }
            }
        }
    }
}