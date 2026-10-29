package com.taqwanet.app.feature.home.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.domain.model.PrayerTime
import com.taqwanet.app.domain.model.PrayerType
import java.time.LocalTime

@Composable
fun PrayerCardsRow(
    prayerList: List<PrayerTime>,
    currentTime: LocalTime,
    onTogglePrayer: (PrayerTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fardhPrayers = prayerList.filter { it.type.isFardh }

    // পুরো ব্যানার কন্টেইনার (ছবিটির মতো ডিপ এমারেল্ড ব্যাকগ্রাউন্ড ও ফ্রেম)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color.Black.copy(alpha = 0.8f)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF03241C),
                        Color(0xFF021712),
                        Color(0xFF010E0B)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clip(RoundedCornerShape(26.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 14.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ১. উপরের রাজকীয় মেডেলিয়ন টাইটেল ও দুই পাশের লণ্ঠন
            BannerHeaderWithLanterns()

            Spacer(modifier = Modifier.height(12.dp))

            // ২. এক স্ক্রিনেই ৫টি কার্ড পাশাপাশি (No Horizontal Scroll - Perfect Weight Grid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                fardhPrayers.forEach { prayer ->
                    val isEligible = prayer.isCurrent || currentTime.isAfter(prayer.time)

                    ExactMockupPrayerCard(
                        prayer = prayer,
                        isEligible = isEligible,
                        modifier = Modifier.weight(1f),
                        onToggle = {
                            if (isEligible) {
                                onTogglePrayer(prayer)
                            } else {
                                Toast.makeText(
                                    context,
                                    "${prayer.type.banglaName} নামাজের ওয়াক্ত এখনো শুরু হয়নি।",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

// ওপরে মেডেলিয়ন ও লণ্ঠন আঁকার কম্পোনেন্ট
@Composable
private fun BannerHeaderWithLanterns() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        // দুই পাশের লণ্ঠন (Fanus)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanternIcon()
            LanternIcon()
        }

        // মাঝের গোল্ডেন ইসলামিক মেডেলিয়ন ফ্রেম
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF062B21),
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(AntiqueGold, SoftGold, AntiqueGold))
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "নামাজের সময়সূচি",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBF4DC)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ঝুলন্ত সোনালী লণ্ঠন আইকন
@Composable
private fun LanternIcon() {
    Icon(
        imageVector = Icons.Default.Lightbulb,
        contentDescription = "Lantern",
        tint = AntiqueGold,
        modifier = Modifier.size(20.dp)
    )
}

// রেফারেন্স ছবির হুবহু একক কার্ড
@Composable
private fun ExactMockupPrayerCard(
    prayer: PrayerTime,
    isEligible: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val isCurrent = prayer.isCurrent

    // ১২ ঘণ্টার সময় ফরম্যাট
    val hour12 = if (prayer.time.hour % 12 == 0) 12 else prayer.time.hour % 12
    val minuteStr = String.format("%02d", prayer.time.minute)
    val timeFormatted = "$hour12:$minuteStr".toBanglaDigits()
    val amPmStr = if (prayer.time.hour < 12) "AM" else "PM"

    // চলমান ওয়াক্তে তীব্র সোনালী আলোর নিয়ন বর্ডার ও শ্যাডো
    val cardBorder = if (isCurrent) {
        BorderStroke(
            2.dp,
            Brush.verticalGradient(
                listOf(
                    Color(0xFFFFEA7A),
                    AntiqueGold,
                    Color(0xFFFFB703)
                )
            )
        )
    } else {
        BorderStroke(1.dp, Color(0xFF145945).copy(alpha = 0.8f))
    }

    val cardBackground = if (isCurrent) {
        Brush.verticalGradient(listOf(Color(0xFF083327), Color(0xFF031E17)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF04201A), Color(0xFF021612)))
    }

    // ছবির আইকনগুলো
    val prayerIcon = when (prayer.type) {
        PrayerType.FAJR -> Icons.Default.WbTwilight
        PrayerType.DHUHR -> Icons.Default.WbSunny
        PrayerType.ASR -> Icons.Default.WbSunny
        PrayerType.MAGHRIB -> Icons.Default.Mosque
        PrayerType.ISHA -> Icons.Default.NightlightRound
        else -> Icons.Default.Schedule
    }

    val glowColor = if (isCurrent) AntiqueGold else MintBright

    Card(
        modifier = modifier
            .height(152.dp)
            .shadow(
                elevation = if (isCurrent) 14.dp else 2.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (isCurrent) Color(0xFFFFC107).copy(alpha = 0.7f) else Color.Transparent
            ),
        shape = RoundedCornerShape(18.dp),
        border = cardBorder
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBackground)
        ) {
            // কার্ডের ভেতরে ওপরের মেহরাব খিলান আর্ট
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val archPath = Path().apply {
                    moveTo(w * 0.12f, 0f)
                    lineTo(w * 0.12f, 16.dp.toPx())
                    cubicTo(
                        w * 0.25f, 26.dp.toPx(),
                        w * 0.75f, 26.dp.toPx(),
                        w * 0.88f, 16.dp.toPx()
                    )
                    lineTo(w * 0.88f, 0f)
                }
                drawPath(
                    path = archPath,
                    color = if (isCurrent) AntiqueGold.copy(alpha = 0.25f) else Color(0xFF19634D).copy(alpha = 0.35f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp, horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ১. সময় ও AM/PM
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color(0xFFFFF0A6) else DarkTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = amPmStr,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCurrent) AntiqueGold else DarkTextSecondary
                    )
                }

                // ২. প্রতীকী আইকন
                Icon(
                    imageVector = prayerIcon,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier.size(23.dp)
                )

                // ৩. ওয়াক্তের নাম
                Text(
                    text = prayer.type.banglaName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) Color(0xFFFFF0A6) else DarkTextPrimary
                )

                // ৪. রেফারেন্স ইমেজের মতো ডেকোরেটিভ টিক বাটন: — (✔) —
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                ) {
                    // বাম ডেকোরেটিভ লাইন
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(if (isCurrent) AntiqueGold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f))
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    // মূল বৃত্তাকার বাটন
                    Surface(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { onToggle() },
                        shape = CircleShape,
                        color = when {
                            prayer.isCompleted && isCurrent -> AntiqueGold
                            prayer.isCompleted -> CompletedGreen
                            !isEligible -> Color.White.copy(alpha = 0.04f)
                            else -> Color.Transparent
                        },
                        border = BorderStroke(
                            1.2.dp,
                            when {
                                prayer.isCompleted && isCurrent -> Color(0xFFFFE57F)
                                prayer.isCompleted -> MintBright
                                isCurrent -> AntiqueGold
                                !isEligible -> Color.White.copy(alpha = 0.15f)
                                else -> Color.White.copy(alpha = 0.35f)
                            }
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (prayer.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "আদায় সম্পন্ন",
                                    tint = if (isCurrent) Color(0xFF021712) else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (!isEligible) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "লকড",
                                    tint = Color.White.copy(alpha = 0.20f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(3.dp))

                    // ডান ডেকোরেটিভ লাইন
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(if (isCurrent) AntiqueGold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f))
                    )
                }
            }
        }
    }
}