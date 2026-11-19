package com.valcarcel.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valcarcel.centrotareas.data.TareasRepositoryLocal
import com.valcarcel.centrotareas.ui.PantallaTareas
import com.valcarcel.centrotareas.ui.theme.CentroTareasTheme
import com.valcarcel.centrotareas.viewmodel.TareasViewModel
import com.valcarcel.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme {
                val vm: TareasViewModel = viewModel(
                    factory = TareasViewModelFactory(TareasRepositoryLocal())
                )
                PantallaTareas(vm)
            }
        }
    }
}