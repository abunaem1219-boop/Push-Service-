package com.taqwanet.app.feature.qibla

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*
import kotlin.math.roundToInt

@Composable
fun QiblaScreen(
    viewModel: QiblaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        viewModel.startSensors()
        onDispose {
            viewModel.stopSensors()
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = state.needleRotationAngle,
        animationSpec = tween(150),
        label = "compassRotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // হেডার
            Text(
                text = "কিবলা কম্পাস",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${state.cityNameBn} থেকে পবিত্র কাবার দিকনির্দেশনা",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextSecondary
            )

            Spacer(modifier = Modifier.height(30.dp))

            // কিবলা ডিগ্রি ও দূরত্বের ইনফো কার্ড
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("কিবলা কোণ", style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.qiblaBearing.roundToInt().toString().toBanglaDigits()}°",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AntiqueGold
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
                    border = BorderStroke(1.dp, MintBright.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("কাবার দূরত্ব", style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.distanceKm.toString().toBanglaDigits()} কি.মি.",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MintBright
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // প্রিমিয়াম গোলাকার ডায়াল ও কম্পাস
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(EmeraldCardDark, EmeraldSurfaceDark, EmeraldDeepDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // ডায়ালের বৃত্তাকার দাগ ও দাগসমূহ
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // বাইরের বৃত্তের বর্ডার
                    drawCircle(
                        color = AntiqueGold.copy(alpha = 0.4f),
                        radius = radius - 4.dp.toPx(),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )
                }

                // কিবলা নির্দেশক সুই (ঘূর্ণনশীল অংশ)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedRotation),
                    contentAlignment = Alignment.Center
                ) {
                    CompassNeedle()
                }

                // কম্পাসের মাঝের গোল্ডেন পিন
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(AntiqueGold),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldDeepDark)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // সেন্সর ক্যালিগ্রেশন নোট
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EmeraldCardDark.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MintBright, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "নির্ভুল দিকের জন্য ফোনটি সমতল রাখুন এবং ৮ (Eight) আকারে ঘোরান।",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun CompassNeedle() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2

        // উত্তরের কাবার দিকের সুই (সবুজ ও গোল্ডেন)
        val northPath = Path().apply {
            moveTo(centerX, centerY - 110.dp.toPx())
            lineTo(centerX - 14.dp.toPx(), centerY)
            lineTo(centerX + 14.dp.toPx(), centerY)
            close()
        }
        drawPath(northPath, color = MintBright)

        // দক্ষিণের সুই (হালকা মেটালিক)
        val southPath = Path().apply {
            moveTo(centerX, centerY + 110.dp.toPx())
            lineTo(centerX - 14.dp.toPx(), centerY)
            lineTo(centerX + 14.dp.toPx(), centerY)
            close()
        }
        drawPath(southPath, color = Color.White.copy(alpha = 0.25f))

        // সুইয়ের মাথার ওপর পবিত্র কাবার প্রতীক (একটি সোনালী বৃত্ত)
        drawCircle(
            color = AntiqueGold,
            radius = 10.dp.toPx(),
            center = Offset(centerX, centerY - 110.dp.toPx())
        )
    }
}