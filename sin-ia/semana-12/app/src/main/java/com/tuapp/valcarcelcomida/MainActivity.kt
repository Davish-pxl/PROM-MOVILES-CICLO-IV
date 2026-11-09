package com.tuapp.valcarcelcomida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tuapp.valcarcelcomida.ui.navigation.AppNav
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

class MainActivity : ComponentActivity() {

    private val container by lazy { AppContainer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValcarcelComidaTheme {
                AppNav(container)
            }
        }
    }
}