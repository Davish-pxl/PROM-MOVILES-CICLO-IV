package com.valcarcel.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valcarcel.avisoscampus.data.AvisosRepositoryMemoria
import com.valcarcel.avisoscampus.ui.PantallaAvisos
import com.valcarcel.avisoscampus.ui.theme.AvisosCampusTheme
import com.valcarcel.avisoscampus.viewmodel.AvisosViewModel
import com.valcarcel.avisoscampus.viewmodel.AvisosViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvisosCampusTheme {
                val vm: AvisosViewModel = viewModel(
                    factory = AvisosViewModelFactory(AvisosRepositoryMemoria())
                )
                PantallaAvisos(vm)
            }
        }
    }
}