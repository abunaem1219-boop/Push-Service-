package com.taqwanet.app.feature.reels.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.taqwanet.app.core.theme.*

@Composable
fun AuthProfileDialog(
    onDismiss: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    // স্টেটসমূহ
    var isSignUpMode by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceDark),
            border = BorderStroke(1.5.dp, Brush.verticalGradient(listOf(AntiqueGold, MintBright))),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF072920))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ক্লোজ বাটন
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentUser != null) "আপনার প্রোফাইল" else if (isSignUpMode) "নতুন অ্যাকাউন্ট তৈরি" else "লগইন করুন",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AntiqueGold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ", tint = DarkTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (currentUser != null) {
                    // ১. ইউজার লগইন থাকলে প্রোফাইল কার্ড
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MintContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser.displayName?.take(1) ?: "মু").uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MintBright
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser.displayName ?: "ইসলামিক গবেষক",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentUser.email ?: "",
                        fontSize = 13.sp,
                        color = DarkTextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            auth.signOut()
                            Toast.makeText(context, "সফলভাবে লগআউট করা হয়েছে।", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC75146)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("লগআউট করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // ২. লগইন বা সাইন আপ ফর্ম
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("আপনার নাম", color = DarkTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("ইমেইল ঠিকানা", color = DarkTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর)", color = DarkTextSecondary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = err, color = Color(0xFFFF6B6B), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (emailInput.isBlank() || passwordInput.isBlank()) {
                                errorMessage = "অনুগ্রহ করে ইমেইল ও পাসওয়ার্ড লিখুন।"
                                return@Button
                            }
                            if (isSignUpMode && nameInput.isBlank()) {
                                errorMessage = "আপনার নাম লিখুন।"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            if (isSignUpMode) {
                                // নতুন অ্যাকাউন্ট তৈরি (Sign Up)
                                auth.createUserWithEmailAndPassword(emailInput.trim(), passwordInput.trim())
                                    .addOnSuccessListener { result ->
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(nameInput.trim())
                                            .build()
                                        result.user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                                            isLoading = false
                                            Toast.makeText(context, "আলহামদুলিল্লাহ! অ্যাকাউন্ট তৈরি সফল হয়েছে।", Toast.LENGTH_SHORT).show()
                                            onAuthSuccess()
                                            onDismiss()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        errorMessage = e.localizedMessage ?: "অ্যাকাউন্ট তৈরি ব্যর্থ হয়েছে।"
                                    }
                            } else {
                                // লগইন (Login)
                                auth.signInWithEmailAndPassword(emailInput.trim(), passwordInput.trim())
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "স্বাগতম! সফলভাবে লগইন হয়েছে।", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess()
                                        onDismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        errorMessage = e.localizedMessage ?: "লগইন তথ্য সঠিক নয়।"
                                    }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = MintBright),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = EmeraldDeepDark, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isSignUpMode) "অ্যাকাউন্ট খুলুন" else "লগইন করুন",
                                color = EmeraldDeepDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { isSignUpMode = !isSignUpMode; errorMessage = null }) {
                        Text(
                            text = if (isSignUpMode) "ইতিমধ্যে অ্যাকাউন্ট আছে? লগইন করুন" else "নতুন অ্যাকাউন্ট প্রয়োজন? সাইন আপ করুন",
                            color = AntiqueGold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}