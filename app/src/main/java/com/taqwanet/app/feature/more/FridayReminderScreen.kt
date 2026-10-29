package com.taqwanet.app.feature.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.theme.*

@Composable
fun FridayReminderScreen(onBack: () -> Unit) {
    val fridaySunnahs = listOf(
        "গোসল করা এবং পরিষ্কার-পরিচ্ছন্ন পোশাক পরিধান করা।",
        "সুগন্ধি (আতর) ও মিসওয়াক ব্যবহার করা।",
        "সূরা আল-কাহফ তিলাওয়াত করা (যা দুই জুমার মাঝে আলোর দিশারী হয়)।",
        "রাসূলুল্লাহ (সা.)-এর প্রতি অধিক পরিমাণে দরুদ শরীফ পাঠ করা।",
        "জুমার খুতবা ও নামাজের জন্য সকাল সকাল মসজিদে যাওয়া।",
        "আসরের পর থেকে মাগরিব পর্যন্ত দোয়া কবুলের মুহূর্তে একাগ্রচিত্তে দোয়া করা।"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "পিছনে", tint = MintBright)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "জুমার প্রস্তুতি ও সুন্নাত",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "জুম্মা মুবারক!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "সপ্তাহের শ্রেষ্ঠ দিন জুমাবার। এ দিনের আমল ও বরকত সম্পর্কে রাসূলুল্লাহ (সা.) বিশেষ গুরুত্ব দিয়েছেন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextSecondary
                        )
                    }
                }
            }

            items(fridaySunnahs.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldCardDark.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintBright, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = fridaySunnahs[index],
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextPrimary
                        )
                    }
                }
            }
        }
    }
}