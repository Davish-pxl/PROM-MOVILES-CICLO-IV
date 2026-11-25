package com.valcarcel.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valcarcel.murocampus.data.PostRepositoryRemote
import com.valcarcel.murocampus.data.remote.RetrofitClient
import com.valcarcel.murocampus.ui.PantallaLista
import com.valcarcel.murocampus.ui.theme.MuroCampusTheme
import com.valcarcel.murocampus.viewmodel.PostsViewModel
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {

    private val vm: PostsViewModel by viewModels {
        viewModelFactory {
            initializer { PostsViewModel(PostRepositoryRemote(RetrofitClient.api)) }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
                    PantallaLista(
                        vm = vm,
                        modifier = Modifier.padding(innerPadding)
                    )
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