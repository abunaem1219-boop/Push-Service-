package com.taqwanet.app.feature.quran.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.AntiqueGold
import com.taqwanet.app.feature.quran.HafeziQuranData
import java.io.InputStream
import kotlin.math.abs

enum class ReadingTheme(val bgColor: Color, val textColor: Color, val borderGold: Color) {
    ORIGINAL(Color(0xFFFAF8F2), Color(0xFF1B1B1B), Color(0xFFD4AF37)),
    SEPIA(Color(0xFFF3E9D2), Color(0xFF2C2416), Color(0xFFB58E2E)),
    NIGHT(Color(0xFF121A17), Color(0xFFE0E7E4), Color(0xFF52B788))
}

@Composable
fun HafeziQuranPageViewer(
    pagerState: PagerState,
    theme: ReadingTheme,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        pageSpacing = 0.dp
    ) { pageIndex ->
        val pageNumber = pageIndex + 1

        // ভিডিওর মতো ১৮০° পেজ-টার্ন ক্যালকুলেশন
        val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction

        // zIndex নিশ্চিত করে উল্টানো পাতাটি সবসময় ওপরের স্তরে থাকবে
        val calculatedZIndex = if (pageOffset in -1.0f..1.0f) {
            1.0f - abs(pageOffset)
        } else {
            0.0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(calculatedZIndex)
                .graphicsLayer {
                    cameraDistance = 24f * density // গভীর ৩D চোখের দূরত্ব

                    if (pageOffset in -1.0f..0.0f) {
                        // পাতাটি যখন ডান দিক থেকে উল্টে বামে যাচ্ছে
                        val progress = abs(pageOffset)
                        transformOrigin = TransformOrigin(0f, 0.5f) // বইয়ের মূল স্পাইন/বাঁধাই স্থির থাকবে
                        rotationY = -180f * progress // পূর্ণ ১৮০ ডিগ্রি কোণে উল্টানো
                        translationX = pageOffset * size.width // স্পাইন এক জায়গায় ধরে রাখার কাউন্টার-মুভমেন্ট
                    } else if (pageOffset in 0.0f..1.0f) {
                        // নিচের পাতাটি স্থির থাকবে
                        transformOrigin = TransformOrigin(0f, 0.5f)
                        rotationY = 0f
                        translationX = 0f
                    }
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(theme.bgColor)
        ) {
            // যদি পাতাটি উল্টে ৯০ ডিগ্রির বেশি চলে যায়, তখন পাতার পেছনের পিঠ (Backface) দেখানো হবে
            val isBackface = pageOffset < -0.5f

            if (isBackface) {
                // উল্টানো পাতার পেছনের পাতার পিঠ ও আলোর শেড
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f } // লেখার মিরর ঠিক রাখা
                        .background(theme.bgColor.copy(alpha = 0.96f))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.22f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.06f)
                                )
                            )
                        )
                    }
                }
            } else {
                // পাতার আসল ছবি লোড করা (assets/quran/page_xxx.webp থেকে)
                val bitmap = remember(pageNumber) {
                    try {
                        val assetPath = String.format("quran/page_%03d.webp", pageNumber)
                        val stream: InputStream = context.assets.open(assetPath)
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    } catch (e: Exception) {
                        try {
                            val assetJpg = String.format("quran/page_%03d.jpg", pageNumber)
                            val stream: InputStream = context.assets.open(assetJpg)
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        } catch (ex: Exception) {
                            null
                        }
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "পৃষ্ঠা নম্বর $pageNumber",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // ছবি না থাকলে এই মেহরাব খোদাই করা পাতাটি রেন্ডার হবে
                    DefaultHafeziPagePlaceholder(pageNumber = pageNumber, theme = theme)
                }

                // পাতা উল্টানোর সময় কাগজের ভাঁজের ত্রিমাত্রিক ছায়া (Crease Shadow Overlay)
                val shadowAlpha = (abs(pageOffset) * 0.45f).coerceIn(0f, 0.45f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // বইয়ের মাঝখানের স্পাইন শ্যাডো
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.28f + shadowAlpha),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.08f)
                            ),
                            startX = 0f,
                            endX = size.width * 0.25f
                        )
                    )
                }
            }
        }
    }
}

// ছবি না থাকলে প্রদর্শিত মেহরাব আর্ট
@Composable
private fun DefaultHafeziPagePlaceholder(pageNumber: Int, theme: ReadingTheme) {
    val juz = HafeziQuranData.getJuzForPage(pageNumber)
    val surah = HafeziQuranData.getSurahForPage(pageNumber)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRoundRect(
                color = theme.borderGold.copy(alpha = 0.45f),
                style = Stroke(width = 2.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
            )
            drawRoundRect(
                color = theme.borderGold.copy(alpha = 0.22f),
                topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(w - 12.dp.toPx(), h - 12.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "পারা ${juz.number.toString().toBanglaDigits()} • ${juz.nameBn}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textColor.copy(alpha = 0.75f)
                )
                Text(
                    text = "সূরা ${surah.nameBn}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textColor.copy(alpha = 0.75f)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AntiqueGold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "১৫ লাইনের নূরানী হাফেজী কুরআন",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "পৃষ্ঠার ছবি যুক্ত করতে আপনার ছবি\n'assets/quran/page_${String.format("%03d", pageNumber)}.webp'\nফোল্ডারে রাখলেই আসল কিতাবের পাতা ভেসে উঠবে।",
                    fontSize = 12.sp,
                    color = theme.textColor.copy(alpha = 0.6f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Text(
                text = "— পৃষ্ঠা ${pageNumber.toString().toBanglaDigits()} —",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = theme.borderGold
            )
        }
    }
}