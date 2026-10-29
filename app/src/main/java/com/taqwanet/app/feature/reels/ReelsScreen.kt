package com.taqwanet.app.feature.reels

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.taqwanet.app.core.calculation.toBanglaDigits
import com.taqwanet.app.core.theme.*
import com.taqwanet.app.data.repository.ReelsRepository
import com.taqwanet.app.domain.model.ReelComment
import com.taqwanet.app.domain.model.ReelItem
import com.taqwanet.app.feature.reels.components.*
import kotlinx.coroutines.launch

@Composable
fun ReelsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ReelsRepository(context) }
    val auth = remember { FirebaseAuth.getInstance() }

    // অত্যন্ত দ্রুত ও কার্যকরী ফলব্যাক ইসলামিক ভিডিও (কালো স্ক্রিন মুক্ত)
    val defaultReels = remember {
        listOf(
            ReelItem(
                id = "default_1",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                title = "আল্লাহর রহমত থেকে কখনও নিরাশ হবেন না",
                uploaderName = "শায়েখ আহমাদুল্লাহ",
                likesCount = 142,
                commentsCount = 18
            ),
            ReelItem(
                id = "default_2",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                title = "ধৈর্য ও সালাতের অলৌকিক শক্তি",
                uploaderName = "মিজানুর রহমান আজহারী",
                likesCount = 289,
                commentsCount = 34
            )
        )
    }

    val liveReels by repository.getReelsStream().collectAsState(initial = defaultReels)
    val reelsList = if (liveReels.isEmpty()) defaultReels else liveReels

    val pagerState = rememberPagerState(pageCount = { reelsList.size })

    // ডায়ালগ স্টেটসমূহ
    var isAuthDialogOpen by remember { mutableStateOf(false) }
    var isUploadDialogOpen by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var isCommentsOpen by remember { mutableStateOf(false) }
    val currentReel = reelsList.getOrNull(pagerState.currentPage) ?: reelsList.first()

    var activeComments by remember { mutableStateOf<List<ReelComment>>(emptyList()) }

    LaunchedEffect(currentReel.id) {
        if (currentReel.id.startsWith("default_")) {
            activeComments = listOf(
                ReelComment(userName = "আব্দুল্লাহ", commentText = "মাশাআল্লাহ, অত্যন্ত গুরুত্বপূর্ণ নসিহত!"),
                ReelComment(userName = "সালমান", commentText = "আল্লাহ আমাদের সবাইকে আমল করার তৌফিক দান করুন।")
            )
        } else {
            repository.getCommentsStream(currentReel.id).collect { list ->
                activeComments = list
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ১. শুধুমাত্র পেছনের ভিডিও সারফেসগুলো স্ক্রোল হবে (Background Layer)
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val reel = reelsList[pageIndex]
            val isCurrentPage = pagerState.currentPage == pageIndex

            ReelVideoPlayer(
                videoUrl = reel.videoUrl,
                isPlaying = isCurrentPage
            )
        }

        // ২. স্থির ফোরগ্রাউন্ড ওভারলে (Pinned Overlay HUD - আইকনগুলো স্ক্রিনে স্থির থাকবে)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp) // বটম নেভিগেশন বারের ওপরে মার্জিন
        ) {
            // ক. টপ বার (আপলোড ও প্রোফাইল বাটন স্থির থাকবে)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ইসলামিক রিলস",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // ভিডিও আপলোড বাটন
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (auth.currentUser == null) {
                                    Toast.makeText(context, "ভিডিও আপলোড করতে প্রথমে অ্যাকাউন্ট খুলুন বা লগইন করুন।", Toast.LENGTH_SHORT).show()
                                    isAuthDialogOpen = true
                                } else {
                                    isUploadDialogOpen = true
                                }
                            },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MintBright.copy(alpha = 0.5f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "আপলোড", tint = MintBright, modifier = Modifier.size(20.dp))
                        }
                    }

                    // প্রোফাইল / অ্যাকাউন্ট বাটন
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { isAuthDialogOpen = true },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (auth.currentUser != null) {
                                Text(
                                    text = (auth.currentUser?.displayName?.take(1) ?: "মু").uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold,
                                    fontSize = 15.sp
                                )
                            } else {
                                Icon(Icons.Default.AccountCircle, contentDescription = "প্রোফাইল", tint = AntiqueGold, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }

            // খ. ডানপাশের সোশ্যাল অ্যাকশন বাটন (এক জায়গায় স্থির থাকবে, শুধু ডেটা আপডেট হবে)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // লাইক বাটন
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            if (auth.currentUser == null) {
                                Toast.makeText(context, "লাইক দিতে দয়া করে অ্যাকাউন্ট খুলুন বা লগইন করুন।", Toast.LENGTH_SHORT).show()
                                isAuthDialogOpen = true
                            } else {
                                coroutineScope.launch {
                                    repository.toggleLike(currentReel)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                    ) {
                        Icon(
                            imageVector = if (currentReel.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "লাইক",
                            tint = if (currentReel.isLikedByMe) Color.Red else Color.White
                        )
                    }
                    Text(
                        text = currentReel.likesCount.toString().toBanglaDigits(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // কমেন্ট বাটন
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { isCommentsOpen = true },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = "কমেন্ট", tint = Color.White)
                    }
                    Text(
                        text = currentReel.commentsCount.toString().toBanglaDigits(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // শেয়ার বাটন
                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "${currentReel.title}\n\nভিডিওটি দেখুন: ${currentReel.videoUrl}\n(তাকওয়ানেট অ্যাপ)")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "রিলস শেয়ার করুন"))
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    Icon(Icons.Default.Share, contentDescription = "শেয়ার", tint = Color.White)
                }
            }

            // গ. নিচের টাইটেল ও ইউজারের নাম (স্ক্রিনে স্থির থাকবে)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 40.dp, end = 85.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MintBright.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintBright, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentReel.uploaderName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintSoft
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentReel.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    lineHeight = 22.sp,
                    maxLines = 3
                )
            }
        }

        // প্রোফাইল ও অ্যাকাউন্ট ডায়ালগ
        if (isAuthDialogOpen) {
            AuthProfileDialog(
                onDismiss = { isAuthDialogOpen = false },
                onAuthSuccess = { isAuthDialogOpen = false }
            )
        }

        // কমেন্ট বটম শীট
        if (isCommentsOpen) {
            CommentsBottomSheet(
                comments = activeComments,
                onSendComment = { commentText ->
                    if (auth.currentUser == null) {
                        Toast.makeText(context, "মন্তব্য করতে দয়া করে অ্যাকাউন্ট খুলুন বা লগইন করুন।", Toast.LENGTH_SHORT).show()
                        isAuthDialogOpen = true
                    } else {
                        coroutineScope.launch {
                            repository.postComment(currentReel.id, commentText)
                        }
                    }
                },
                onDismiss = { isCommentsOpen = false }
            )
        }

        // ভিডিও আপলোড ডায়ালগ
        if (isUploadDialogOpen) {
            UploadReelDialog(
                isUploading = isUploading,
                onUploadConfirmed = { videoUri, title ->
                    isUploading = true
                    coroutineScope.launch {
                        val result = repository.uploadVideoToCloudinary(videoUri, title)
                        isUploading = false
                        if (result.isSuccess) {
                            Toast.makeText(context, "আলহামদুলিল্লাহ! রিলস সফলভাবে আপলোড হয়েছে।", Toast.LENGTH_LONG).show()
                            isUploadDialogOpen = false
                        } else {
                            Toast.makeText(context, "আপলোড ব্যর্থ হয়েছে: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onDismiss = { isUploadDialogOpen = false }
            )
        }
    }
}