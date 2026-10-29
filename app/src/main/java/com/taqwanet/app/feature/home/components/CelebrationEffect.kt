package com.taqwanet.app.feature.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.theme.*
import kotlin.random.Random

@Composable
fun CelebrationEffect(
    visible: Boolean,
    prayerNameBn: String,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val infiniteTransition = rememberInfiniteTransition(label = "petalsFall")
    val fallProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fall"
    )

    // ২০টি ফুলের পাপড়ি ও আলোক কণার প্রাথমিক র‍্যান্ডম অবস্থান
    val particles = remember {
        val rand = Random(55)
        List(24) {
            Triple(
                rand.nextFloat(), // X ratio (0..1)
                rand.nextFloat(), // Speed offset
                if (rand.nextBoolean()) AntiqueGold else MintBright // কালার
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ১. ফুলের পাপড়ি ও আলোক কণা ঝরে পড়ার ক্যানভাস
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            particles.forEach { (xRatio, speedFactor, color) ->
                val currentY = ((fallProgress * (1f + speedFactor) + speedFactor) % 1f) * h
                val currentX = (xRatio * w) + (kotlin.math.sin(currentY / 40.0).toFloat() * 14.dp.toPx())

                // ফুলের পাপড়ির মতো ছোট ডিম্বাকৃতি আঁকা
                drawOval(
                    color = color.copy(alpha = 0.80f),
                    topLeft = Offset(currentX, currentY),
                    size = androidx.compose.ui.geometry.Size(8.dp.toPx(), 13.dp.toPx())
                )
            }
        }

        // ২. মূল অভিনন্দন কার্ড
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldCardDark),
                elevation = CardDefaults.cardElevation(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.sweepGradient(listOf(AntiqueGold, MintBright, AntiqueGold))
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(MintBright, CompletedGreen))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "সম্পন্ন",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "আলহামদুলিল্লাহ!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "আপনার $prayerNameBn সালাত আদায় সম্পন্ন হয়েছে।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = DarkTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "আল্লাহ তাআলা আপনার ইবাদত কবুল করুন।",
                        style = MaterialTheme.typography.labelSmall,
                        color = MintSoft
                    )
                }
            }
        }
    }
}