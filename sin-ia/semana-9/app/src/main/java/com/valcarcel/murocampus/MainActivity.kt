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
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.sp
import com.valcarcel.murocampus.data.PostRepositoryRemote
import com.valcarcel.murocampus.data.remote.RetrofitClient
import com.valcarcel.murocampus.ui.PantallaCrear
import com.valcarcel.murocampus.ui.PantallaDetalle
import com.valcarcel.murocampus.ui.PantallaLista
import com.valcarcel.murocampus.ui.theme.MuroCampusTheme
import com.valcarcel.murocampus.viewmodel.PostsViewModel
import com.valcarcel.murocampus.viewmodel.PostsViewModelFactory
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val vm: PostsViewModel by viewModels {
        PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
            var creando by rememberSaveable { mutableStateOf(false) }
            BackHandler(enabled = idSeleccionado != null || creando) {
                idSeleccionado = null
                creando = false
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
                    },
                    floatingActionButton = {
                        if (idSeleccionado == null && !creando) {
                            FloatingActionButton(
                                onClick = {
                                    vm.limpiarPublicacion()
                                    creando = true
                                },
                                containerColor = Color(0xFF1E6BB0),
                                contentColor = Color.White
                            ) {
                                Text("+", fontSize = 24.sp)
                            }
                        }
                    }
                ) { innerPadding ->
                    val id = idSeleccionado
                    when {
                        creando -> PantallaCrear(
                            vm = vm,
                            modifier = Modifier.padding(innerPadding)
                        )

                        id != null -> PantallaDetalle(
                            id = id,
                            vm = vm,
                            onEliminada = { idSeleccionado = null },
                            modifier = Modifier.padding(innerPadding)
                        )

                        else -> PantallaLista(
                            vm = vm,
                            modifier = Modifier.padding(innerPadding),
                            onAbrir = { nuevoId ->
                                vm.limpiarBorrado()
                                idSeleccionado = nuevoId
                                vm.abrir(nuevoId)
                            }
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