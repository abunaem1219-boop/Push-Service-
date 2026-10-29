package com.taqwanet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.taqwanet.app.core.theme.TaqwaNetTheme
import com.taqwanet.app.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaqwaNetTheme(darkTheme = true) {
                AppNavGraph()
            }
        }
    }
}