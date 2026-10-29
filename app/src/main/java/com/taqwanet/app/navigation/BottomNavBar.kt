package com.taqwanet.app.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.taqwanet.app.core.theme.*

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Home,
        Screen.Prayer,
        Screen.Reels,
        Screen.Qibla,
        Screen.More
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route ?: Screen.Home.route

    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.let {
        if (it == -1) 0 else it
    }

    val haptic = LocalHapticFeedback.current

    // বাইরের ভাসমান কন্টেইনার
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // ১. লিকুইড গ্লাস মূল ক্যাপসুল ডক (The Liquid Glass Pill Dock)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.65f),
                    ambientColor = Color.Black.copy(alpha = 0.45f)
                )
                // ফ্রস্টেড সেমি-ট্রান্সপারেন্ট এমারেল্ড গ্লাস ব্যাকগ্রাউন্ড
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x880E382D), // উপরে কিছুটা উজ্জ্বল ট্রান্সলুসেন্ট
                            Color(0xCC05241C)  // নিচে ঘন ডিপ ট্রান্সলুসেন্ট
                        )
                    ),
                    shape = CircleShape
                )
                .clip(CircleShape)
        ) {
            // ২. লিকুইড কাঁচের উজ্জ্বল রিম ও অপটিক্যাল রিফ্লেকশন (Glossy Liquid Rim Border)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f),       // একদম ওপরের কাঁচের চকচকে হাইলাইট
                                AntiqueGold.copy(alpha = 0.20f),       // মাঝের সোনালী হালকা আভা
                                Color.White.copy(alpha = 0.04f),
                                Color.Black.copy(alpha = 0.40f)        // তলার দিকে ঘন শেড
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // ৩. অ্যানিমেটেড স্লাইডিং বাবল ও আইটেম গ্রিড
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 5.dp)
            ) {
                val tabWidth = maxWidth / items.size

                // মসৃণ স্প্রিং ফিজিক্সে স্লাইড করার অফসেট অ্যানিমেশন
                val animatedLeftOffset by animateDpAsState(
                    targetValue = tabWidth * selectedIndex,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "bubbleSlide"
                )

                // ৪. অ্যাক্টিভ ডার্ক গ্লাস বাবল (ছবিতে দেখানো ওভাল বাবল)
                Box(
                    modifier = Modifier
                        .offset(x = animatedLeftOffset)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xDD031A14), // ডার্ক ক্রিস্টাল
                                    Color(0xF005241C)
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    // বাবলের সূক্ষ্ম ভেতরের বর্ডার
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        MintBright.copy(alpha = 0.35f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.4f)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                // ৫. পাঁচটি নেভিগেশন আইটেম (Icon + Label)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, screen ->
                        val isSelected = index == selectedIndex

                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.14f else 0.94f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "iconScale"
                        )

                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) MintBright else Color.White.copy(alpha = 0.60f),
                            animationSpec = tween(220),
                            label = "colorAnim"
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null // ডিফল্ট চারকোনা রিপল বন্ধ করে লিকুইড লুক বজায় রাখা
                                ) {
                                    if (!isSelected) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.titleBn,
                                tint = contentColor,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(iconScale)
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = screen.titleBn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}