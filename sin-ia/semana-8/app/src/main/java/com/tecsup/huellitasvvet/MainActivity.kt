package com.tecsup.huellitasvvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.huellitasvvet.model.Rol
import com.tecsup.huellitasvvet.ui.navigation.NavVet
import com.tecsup.huellitasvvet.ui.theme.VetTheme
import com.tecsup.huellitasvvet.viewmodel.SesionViewModel
import com.tecsup.huellitasvvet.viewmodel.VetViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as VetApp
            val sesionVm: SesionViewModel = viewModel(
                factory = VetViewModelFactory {
                    SesionViewModel(app.repository, app.preferencias)
                }
            )
            val oscuro by sesionVm.modoOscuro.collectAsState()
            val perfil by sesionVm.perfilActivo.collectAsState()

            VetTheme(
                rol = if (perfil == Rol.ADMIN.name) Rol.ADMIN else Rol.CLIENTE,
                oscuro = oscuro
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(Modifier.safeDrawingPadding()) {
                        NavVet(sesionVm)
                    }
                }
            }
        }
    }
}