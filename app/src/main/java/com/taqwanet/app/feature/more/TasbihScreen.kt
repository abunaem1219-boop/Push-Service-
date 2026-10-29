package com.taqwanet.app.feature.more

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*

@Composable
fun TasbihScreen(onBack: () -> Unit) {
    var count by remember { mutableIntStateOf(0) }
    var target by remember { mutableIntStateOf(33) }
    var selectedZikrIndex by remember { mutableIntStateOf(0) }
    var isPressed by remember { mutableStateOf(false) }

    val zikrList = listOf(
        Pair("سُبْحَانَ اللَّهِ", "সুবহানাল্লাহ (আল্লাহ মহাপবিত্র)"),
        Pair("الْحَمْدُ لِلَّهِ", "আলহামদুলিল্লাহ (সকল প্রশংসা আল্লাহর)"),
        Pair("اللَّهُ أَكْبَرُ", "আল্লাহু আকবার (আল্লাহ সর্বশ্রেষ্ঠ)"),
        Pair("لَا إِلٰهَ إِلَّا اللّٰهُ", "লা ইলাহা ইল্লাল্লাহ (আল্লাহ ছাড়া উপাস্য নেই)")
    )

    val haptic = LocalHapticFeedback.current
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(80),
        label = "tasbihScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "ডিজিটাল তসবিহ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // জিকির নির্বাচন ড্রপডাউন/কার্ড
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = zikrList[selectedZikrIndex].first,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = zikrList[selectedZikrIndex].second,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // টার্গেট সিলেকশন বাটন (৩৩, ৯৯, ১০০)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf(33, 99, 100).forEach { tgt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (target == tgt) MintContainer else EmeraldCardDark,
                    border = BorderStroke(1.dp, if (target == tgt) MintBright else Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clickable { target = tgt }
                ) {
                    Text(
                        text = "টার্গেট: ${tgt.toString().toBanglaDigits()}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (target == tgt) MintSoft else DarkTextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // মূল তসবিহ ক্লিক কাউন্টার বাটন (বিশাল ডায়াল)
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(buttonScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(EmeraldCardDark, EmeraldSurfaceDark, EmeraldDeepDark)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isPressed = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    count++
                    if (count >= target) {
                        count = 0
                        selectedZikrIndex = (selectedZikrIndex + 1) % zikrList.size
                    }
                    isPressed = false
                },
            contentAlignment = Alignment.Center
        ) {
            // গোল্ডেন আউটলাইন বৃত্ত
            Surface(
                modifier = Modifier.size(210.dp),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(4.dp, Brush.sweepGradient(listOf(MintBright, AntiqueGold, MintBright)))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = count.toString().toBanglaDigits(),
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    Text(
                        text = "লক্ষ্য: ${target.toString().toBanglaDigits()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MintSoft
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "চাপুন",
                        style = MaterialTheme.typography.labelSmall,
                        color = AntiqueGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // রিসেট বোতাম
        OutlinedButton(
            onClick = {
                count = 0
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTextSecondary)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("পুনরায় শুরু করুন (Reset)", style = MaterialTheme.typography.labelMedium)
        }
    }
}