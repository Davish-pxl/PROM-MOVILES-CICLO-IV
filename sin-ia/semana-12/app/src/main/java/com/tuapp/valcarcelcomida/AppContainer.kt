package com.tuapp.valcarcelcomida

import android.content.Context
import com.tuapp.valcarcelcomida.data.auth.AuthRepository
import com.tuapp.valcarcelcomida.data.auth.FirebaseAuthRepository
import com.tuapp.valcarcelcomida.data.local.AppDatabase
import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.data.remote.RetrofitClient
import com.tuapp.valcarcelcomida.data.repository.ItemRepository
import com.tuapp.valcarcelcomida.data.repository.ItemRepositoryImpl
import com.tuapp.valcarcelcomida.data.repository.Traductor
import com.tuapp.valcarcelcomida.data.repository.TraductorMlKit
import com.tuapp.valcarcelcomida.notification.Notificador
import com.tuapp.valcarcelcomida.notification.NotificadorAndroid

class AppContainer(context: Context) {

    private val api: ApiService = RetrofitClient.api

    private val database: AppDatabase = AppDatabase.obtener(context)

    private val traductor: Traductor by lazy { TraductorMlKit() }
    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(api, database.itemDao(), traductor)
    }

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }

    val notificador: Notificador by lazy { NotificadorAndroid(context) }
}