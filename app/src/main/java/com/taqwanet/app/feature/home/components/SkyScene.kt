package com.taqwanet.app.feature.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.taqwanet.app.core.theme.EmeraldDeepDark
import java.time.LocalTime

@Composable
fun SkyScene(currentTime: LocalTime, modifier: Modifier = Modifier) {
    val hour = currentTime.hour

    // মেঘের ধীরস্থির ও মসৃণ ভাসমান অ্যানিমেশন
    val infiniteTransition = rememberInfiniteTransition(label = "skyAnimations")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudDrift"
    )

    // তারার ঝিকমিক (Twinkle) অ্যানিমেশন
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starTwinkle"
    )

    // সময় অনুযায়ী আকাশ, পাহাড় ও আলোর প্যালেট
    val palette = when (hour) {
        in 4..5 -> SkyPalette( // ফজর / ঊষা
            skyGradient = listOf(Color(0xFF0F1A30), Color(0xFF282846), Color(0xFFC85A32)),
            celestialColor = Color(0xFFFFE6A7),
            haloColor = Color(0xFFFF9E00).copy(alpha = 0.12f),
            hillDistant = Color(0xFF1D2238),
            hillMid = Color(0xFF132A29),
            hillNear = Color(0xFF091C19),
            isNight = true,
            isSun = false
        )
        in 6..8 -> SkyPalette( // সকাল / সূর্যোদয়
            skyGradient = listOf(Color(0xFF104E7A), Color(0xFF2E86AB), Color(0xFFF6D167)),
            celestialColor = Color(0xFFFFF3B0),
            haloColor = Color(0xFFFFE169).copy(alpha = 0.18f),
            hillDistant = Color(0xFF165668),
            hillMid = Color(0xFF174D43),
            hillNear = Color(0xFF0C3129),
            isNight = false,
            isSun = true
        )
        in 9..14 -> SkyPalette( // জোহর / দ্বিপ্রহর
            skyGradient = listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFF90CAF9)),
            celestialColor = Color(0xFFFFFDE7),
            haloColor = Color(0xFFFFFFFF).copy(alpha = 0.22f),
            hillDistant = Color(0xFF206A5D),
            hillMid = Color(0xFF1B5E20),
            hillNear = Color(0xFF0E382D),
            isNight = false,
            isSun = true
        )
        in 15..17 -> SkyPalette( // আসর / বিকেল (রেফারেন্স ছবি ৫-এর মতো গোধূলি)
            skyGradient = listOf(Color(0xFF2B213A), Color(0xFF6B3E4E), Color(0xFFDE7456)),
            celestialColor = Color(0xFFFFC67D),
            haloColor = Color(0xFFF28482).copy(alpha = 0.16f),
            hillDistant = Color(0xFF332036),
            hillMid = Color(0xFF262E2A),
            hillNear = Color(0xFF0D241E),
            isNight = false,
            isSun = true
        )
        in 18..19 -> SkyPalette( // মাগরিব / সূর্যাস্ত
            skyGradient = listOf(Color(0xFF1A1128), Color(0xFF431C3E), Color(0xFFC75146)),
            celestialColor = Color(0xFFFFB563),
            haloColor = Color(0xFFE2711D).copy(alpha = 0.15f),
            hillDistant = Color(0xFF1F1B2C),
            hillMid = Color(0xFF162522),
            hillNear = Color(0xFF081C17),
            isNight = true,
            isSun = false
        )
        else -> SkyPalette( // এশা / রাত (রেফারেন্স ছবি ২ ও ৪-এর মতো মায়াবী চাঁদনী রাত)
            skyGradient = listOf(Color(0xFF03101E), Color(0xFF092338), Color(0xFF0C3C4C)),
            celestialColor = Color(0xFFF4F7F6),
            haloColor = Color(0xFF56CFE1).copy(alpha = 0.14f),
            hillDistant = Color(0xFF082631),
            hillMid = Color(0xFF0D3636),
            hillNear = Color(0xFF06231E),
            isNight = true,
            isSun = false
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(245.dp)
    ) {
        val width = size.width
        val height = size.height

        // ১. আকাশের স্মুথ গ্রেডিয়েন্ট ব্যাকগ্রাউন্ড
        drawRect(
            brush = Brush.verticalGradient(palette.skyGradient)
        )

        val celestialX = width * 0.76f
        val celestialY = height * 0.28f

        // ২. কনসেন্ট্রিক লাইট হ্যালো রিং (রেফারেন্স ছবির মতো ৩ স্তরের আলোর তরঙ্গ)
        drawCircle(
            color = palette.haloColor.copy(alpha = palette.haloColor.alpha * 0.4f),
            radius = 110.dp.toPx(),
            center = Offset(celestialX, celestialY)
        )
        drawCircle(
            color = palette.haloColor.copy(alpha = palette.haloColor.alpha * 0.7f),
            radius = 75.dp.toPx(),
            center = Offset(celestialX, celestialY)
        )
        drawCircle(
            color = palette.haloColor,
            radius = 45.dp.toPx(),
            center = Offset(celestialX, celestialY)
        )

        // ৩. মহাজাগতিক বডি (চাঁদ বা সূর্য)
        drawCircle(
            color = palette.celestialColor,
            radius = 24.dp.toPx(),
            center = Offset(celestialX, celestialY)
        )

        // রাতে চাঁদের ভেতরের হালকা ক্রেটার (Craters)
        if (palette.isNight) {
            drawCircle(
                color = Color.Black.copy(alpha = 0.08f),
                radius = 6.dp.toPx(),
                center = Offset(celestialX - 7.dp.toPx(), celestialY - 5.dp.toPx())
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.06f),
                radius = 4.dp.toPx(),
                center = Offset(celestialX + 6.dp.toPx(), celestialY + 7.dp.toPx())
            )
        }

        // ৪. তারা ও স্পার্কল স্টারস (ছবি ১ ও ২-এর মতো ডায়মন্ড স্টারস)
        if (palette.isNight) {
            drawDiamondStar(Offset(width * 0.12f, height * 0.18f), 6.dp.toPx(), starAlpha)
            drawDiamondStar(Offset(width * 0.38f, height * 0.26f), 4.5.dp.toPx(), starAlpha * 0.8f)
            drawDiamondStar(Offset(width * 0.90f, height * 0.14f), 5.5.dp.toPx(), starAlpha * 0.9f)
            drawDiamondStar(Offset(width * 0.60f, height * 0.42f), 4.dp.toPx(), starAlpha * 0.7f)

            // সাধারণ বিন্দু তারা
            drawCircle(Color.White.copy(alpha = starAlpha * 0.7f), 1.5.dp.toPx(), Offset(width * 0.22f, height * 0.10f))
            drawCircle(Color.White.copy(alpha = starAlpha * 0.6f), 1.2.dp.toPx(), Offset(width * 0.48f, height * 0.14f))
            drawCircle(Color.White.copy(alpha = starAlpha * 0.8f), 1.6.dp.toPx(), Offset(width * 0.85f, height * 0.32f))
            drawCircle(Color.White.copy(alpha = starAlpha * 0.5f), 1.0.dp.toPx(), Offset(width * 0.30f, height * 0.38f))
        }

        // ৫. ফ্ল্যাট স্টাইলাইজড পিল-ক্লাউডস (Layered Floating Pill Clouds)
        val cloudColor = if (palette.isNight) Color(0x3856CFE1) else Color(0x55FFFFFF)

        // পেছনের মেঘ
        drawPillCloud(
            center = Offset(width * 0.28f + cloudOffset * 0.6f, height * 0.22f),
            width = 110.dp.toPx(),
            height = 20.dp.toPx(),
            color = cloudColor.copy(alpha = cloudColor.alpha * 0.7f)
        )
        // চাঁদের সামনের মেঘ
        drawPillCloud(
            center = Offset(celestialX + 25.dp.toPx() - cloudOffset, celestialY + 16.dp.toPx()),
            width = 125.dp.toPx(),
            height = 22.dp.toPx(),
            color = cloudColor
        )
        // নিচের দিকের অনুভূমিক মেঘ
        drawPillCloud(
            center = Offset(width * 0.55f + cloudOffset, height * 0.46f),
            width = 95.dp.toPx(),
            height = 18.dp.toPx(),
            color = cloudColor.copy(alpha = cloudColor.alpha * 0.5f)
        )

        // ৬. দূরবর্তী টিলা/পাহাড়ের স্তর (Distant Rolling Hill)
        val hillDistantPath = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.74f)
            quadraticBezierTo(width * 0.35f, height * 0.60f, width * 0.72f, height * 0.76f)
            quadraticBezierTo(width * 0.88f, height * 0.82f, width, height * 0.78f)
            lineTo(width, height)
            close()
        }
        drawPath(hillDistantPath, color = palette.hillDistant)

        // ৭. মধ্যবর্তী টিলা (Mid Rolling Hill)
        val hillMidPath = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.84f)
            quadraticBezierTo(width * 0.22f, height * 0.70f, width * 0.55f, height * 0.78f)
            quadraticBezierTo(width * 0.80f, height * 0.85f, width, height * 0.73f)
            lineTo(width, height)
            close()
        }
        drawPath(hillMidPath, color = palette.hillMid)

        // ৮. রাজকীয় ইসলামিক মসজিদ এবং তার উষ্ণ সোনালী আলোকিত জানালা
        drawMosqueWithLighting(width, height)

        // ৯. সামনের সবুজ টিলা (Foreground Hill - মসজিদের পাদদেশ)
        val hillNearPath = Path().apply {
            moveTo(0f, height)
            lineTo(0f, height * 0.88f)
            quadraticBezierTo(width * 0.48f, height * 0.81f, width, height * 0.89f)
            lineTo(width, height)
            close()
        }
        drawPath(hillNearPath, color = palette.hillNear)

        // ১০. নিচের কন্টেন্টের সাথে গভীর স্মুথ ব্লেন্ড
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, EmeraldDeepDark.copy(alpha = 0.95f), EmeraldDeepDark),
                startY = height * 0.75f,
                endY = height
            )
        )
    }
}

// ৪-কোণা ডায়মন্ড স্টার আঁকার হেল্পার
private fun DrawScope.drawDiamondStar(center: Offset, size: Float, alpha: Float) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + size * 0.28f, center.y)
        lineTo(center.x, center.y + size)
        lineTo(center.x - size * 0.28f, center.y)
        close()

        moveTo(center.x - size, center.y)
        lineTo(center.x, center.y + size * 0.28f)
        lineTo(center.x + size, center.y)
        lineTo(center.x, center.y - size * 0.28f)
        close()
    }
    drawPath(path, color = Color.White.copy(alpha = alpha))
}

// রেফারেন্স ছবির মতো পিল-ক্লাউড আঁকার হেল্পার
private fun DrawScope.drawPillCloud(center: Offset, width: Float, height: Float, color: Color) {
    drawRoundRect(
        color = color,
        topLeft = Offset(center.x - width / 2, center.y - height / 2),
        size = Size(width, height),
        cornerRadius = CornerRadius(height / 2, height / 2)
    )
}

// উষ্ণ আলো সহ ইসলামিক মসজিদ ড্রয়িং
private fun DrawScope.drawMosqueWithLighting(canvasWidth: Float, canvasHeight: Float) {
    val baseColor = Color(0xFF041814) // মসৃণ ডিপ এমারেল্ড সিলুয়েট
    val warmLightColor = Color(0xFFFFC043) // রেফারেন্স ছবির মতো জানালার উষ্ণ সোনালী আলো
    val lightGlow = Color(0x66FFB703)

    val centerX = canvasWidth * 0.46f
    val baseY = canvasHeight * 0.86f

    // ক. বামদিকের সুন্দর মিনার
    val leftMinaretX = canvasWidth * 0.16f
    drawRect(
        color = baseColor,
        topLeft = Offset(leftMinaretX - 7.dp.toPx(), baseY - 88.dp.toPx()),
        size = Size(14.dp.toPx(), 88.dp.toPx())
    )
    // মিনারের চূড়া
    val leftSpire = Path().apply {
        moveTo(leftMinaretX - 9.dp.toPx(), baseY - 88.dp.toPx())
        lineTo(leftMinaretX, baseY - 108.dp.toPx())
        lineTo(leftMinaretX + 9.dp.toPx(), baseY - 88.dp.toPx())
        close()
    }
    drawPath(leftSpire, color = baseColor)

    // খ. ডানদিকের মিনার
    val rightMinaretX = canvasWidth * 0.78f
    drawRect(
        color = baseColor,
        topLeft = Offset(rightMinaretX - 7.dp.toPx(), baseY - 88.dp.toPx()),
        size = Size(14.dp.toPx(), 88.dp.toPx())
    )
    val rightSpire = Path().apply {
        moveTo(rightMinaretX - 9.dp.toPx(), baseY - 88.dp.toPx())
        lineTo(rightMinaretX, baseY - 108.dp.toPx())
        lineTo(rightMinaretX + 9.dp.toPx(), baseY - 88.dp.toPx())
        close()
    }
    drawPath(rightSpire, color = baseColor)

    // গ. মূল মসজিদ ভবন ও দেয়াল
    val buildingWidth = 140.dp.toPx()
    val buildingHeight = 52.dp.toPx()
    drawRect(
        color = baseColor,
        topLeft = Offset(centerX - buildingWidth / 2, baseY - buildingHeight),
        size = Size(buildingWidth, buildingHeight)
    )

    // ঘ. রাজকীয় সেন্ট্রাল গম্বুজ (Dome)
    val domePath = Path().apply {
        moveTo(centerX - 46.dp.toPx(), baseY - buildingHeight)
        cubicTo(
            centerX - 44.dp.toPx(), baseY - buildingHeight - 44.dp.toPx(),
            centerX - 10.dp.toPx(), baseY - buildingHeight - 58.dp.toPx(),
            centerX, baseY - buildingHeight - 62.dp.toPx()
        )
        cubicTo(
            centerX + 10.dp.toPx(), baseY - buildingHeight - 58.dp.toPx(),
            centerX + 44.dp.toPx(), baseY - buildingHeight - 44.dp.toPx(),
            centerX + 46.dp.toPx(), baseY - buildingHeight
        )
        close()
    }
    drawPath(domePath, color = baseColor)

    // ঙ. জাদুকরী উষ্ণ আলো: গম্বুজের ওপরের ছোট বৃত্তাকার রোশনাই
    drawCircle(
        color = warmLightColor,
        radius = 3.5.dp.toPx(),
        center = Offset(centerX, baseY - buildingHeight - 24.dp.toPx())
    )

    // চ. মসজিদের খিলানযুক্ত উষ্ণ জানালা ও প্রবেশদ্বার (Warm Glowing Windows)
    val windowSpacing = 24.dp.toPx()
    listOf(-1.5f, -0.5f, 0.5f, 1.5f).forEach { pos ->
        val winX = centerX + (pos * windowSpacing)
        val winY = baseY - 28.dp.toPx()

        // জানালার বাইরের মৃদু আলোর আভা (Aura)
        drawCircle(
            color = lightGlow,
            radius = 9.dp.toPx(),
            center = Offset(winX, winY)
        )
        // খিলানযুক্ত উজ্জ্বল হলুদ/কমলা বাতির জানালা
        drawRoundRect(
            color = warmLightColor,
            topLeft = Offset(winX - 5.dp.toPx(), winY - 7.dp.toPx()),
            size = Size(10.dp.toPx(), 14.dp.toPx()),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
        )
    }
}

private data class SkyPalette(
    val skyGradient: List<Color>,
    val celestialColor: Color,
    val haloColor: Color,
    val hillDistant: Color,
    val hillMid: Color,
    val hillNear: Color,
    val isNight: Boolean,
    val isSun: Boolean
)