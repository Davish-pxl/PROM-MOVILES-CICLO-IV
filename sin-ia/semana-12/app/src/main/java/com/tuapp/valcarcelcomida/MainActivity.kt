package com.tuapp.valcarcelcomida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuapp.valcarcelcomida.ui.lista.ListaScreen
import com.tuapp.valcarcelcomida.ui.lista.ListaViewModel
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

class MainActivity : ComponentActivity() {

    private val container by lazy { AppContainer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValcarcelComidaTheme {
                val vm: ListaViewModel = viewModel(factory = ListaViewModel.Factory(container))
                val estado by vm.estado.collectAsStateWithLifecycle()
                val aviso by vm.aviso.collectAsStateWithLifecycle()

                ListaScreen(
                    estado = estado,
                    aviso = aviso,
                    onReintentar = vm::refrescar,
                    onItemClick = { /* H7: navegar al detalle */ },
                    onFavoritoClick = vm::alternarFavorito
                )
            }
        }
    }
}