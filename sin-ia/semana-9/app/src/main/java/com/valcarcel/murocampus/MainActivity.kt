package com.valcarcel.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.valcarcel.murocampus.data.PostRepositoryRemote
import com.valcarcel.murocampus.data.remote.RetrofitClient
import com.valcarcel.murocampus.ui.PantallaDetalle
import com.valcarcel.murocampus.ui.PantallaLista
import com.valcarcel.murocampus.ui.theme.MuroCampusTheme
import com.valcarcel.murocampus.viewmodel.PostsViewModel
import com.valcarcel.murocampus.viewmodel.PostsViewModelFactory
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {

    // La Factory le entrega el Repository al ViewModel.
    // by viewModels hace que el ViewModel sobreviva a la rotación.
    private val vm: PostsViewModel by viewModels {
        PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }

            BackHandler(enabled = idSeleccionado != null) {
                idSeleccionado = null
            }
            MuroCampusTheme(dynamicColor = false) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("MuroCampus", fontWeight = FontWeight.Bold)
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF1E6BB0),
                                titleContentColor = Color.White
                            )
                        )
                    }
                ) { innerPadding ->
                    val id = idSeleccionado
                    if (id == null) {
                        PantallaLista(
                            vm = vm,
                            modifier = Modifier.padding(innerPadding),
                            onAbrir = { nuevoId ->
                                idSeleccionado = nuevoId
                                vm.abrir(nuevoId)
                            }
                        )
                    } else {
                        PantallaDetalle(
                            id = id,
                            vm = vm,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
fun mensajeDe(e: Throwable): String = when (e) {
    is IOException -> "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."
    is HttpException -> "Error del servidor (${e.code()})"
    is SerializationException -> "Respuesta inesperada"
    else -> "Error desconocido"
}