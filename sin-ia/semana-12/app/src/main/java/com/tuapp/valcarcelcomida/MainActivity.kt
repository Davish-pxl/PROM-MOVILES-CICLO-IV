package com.tuapp.valcarcelcomida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.lista.ListaScreen
import com.tuapp.valcarcelcomida.ui.lista.datosPrueba
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValcarcelComidaTheme {
                ListaScreen(
                    estado = UiState.Exito(datosPrueba),
                    aviso = null,
                    onReintentar = {},
                    onItemClick = {},
                    onFavoritoClick = {}
                )
            }
        }
    }
}