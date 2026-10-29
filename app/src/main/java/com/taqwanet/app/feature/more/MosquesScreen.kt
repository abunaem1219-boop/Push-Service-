package com.taqwanet.app.feature.more

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.theme.*

@Composable
fun MosquesScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
            .padding(20.dp)
    ) {
        // টপ বার
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "পিছনে", tint = MintBright)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "নিকটস্থ মসজিদ ও হালাল স্থান",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // মূল সার্চ কার্ড (গুগল ম্যাপস ইন্টেন্ট)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
            border = BorderStroke(1.dp, MintBright.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "আপনার আশেপাশের মসজিদ খুঁজুন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "এক ট্যাপে আপনার বর্তমান জিপিএস অবস্থান অনুযায়ী গুগল ম্যাপসে নিকটস্থ সব মসজিদ ও জামাতের রাস্তা দেখুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val mapUri = Uri.parse("geo:0,0?q=mosque")
                        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            // যদি গুগল ম্যাপস অ্যাপ না থাকে, তবে ব্রাউজারে খুলবে
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/mosque"))
                            context.startActivity(webIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MintBright)
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = EmeraldDeepDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "গুগল ম্যাপসে মসজিদ দেখুন",
                        color = EmeraldDeepDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}