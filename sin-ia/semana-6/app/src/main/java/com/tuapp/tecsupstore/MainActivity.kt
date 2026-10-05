package com.tuapp.tecsupstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tuapp.tecsupstore.navigation.AppNavigation
import com.tuapp.tecsupstore.ui.theme.TecsupStoreTheme

class MainActivity   : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TecsupStoreTheme {
                AppNavigation()
            }
        }
    }
}