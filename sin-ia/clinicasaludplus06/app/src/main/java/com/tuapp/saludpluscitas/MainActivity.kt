package com.tuapp.saludpluscitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tuapp.saludpluscitas.navigation.AppNavigation
import com.tuapp.saludpluscitas.ui.theme.ClinicaSaludPlusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClinicaSaludPlusTheme {
                AppNavigation()
            }
        }
    }
}