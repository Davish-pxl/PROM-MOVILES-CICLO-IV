package com.tuapp.valcarcelcomida

import android.content.Context
import com.tuapp.valcarcelcomida.data.auth.AuthRepository
import com.tuapp.valcarcelcomida.data.auth.FirebaseAuthRepository
import com.tuapp.valcarcelcomida.data.local.AppDatabase
import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.data.remote.RetrofitClient
import com.tuapp.valcarcelcomida.data.repository.ItemRepository
import com.tuapp.valcarcelcomida.data.repository.ItemRepositoryImpl

class AppContainer(context: Context) {

    private val api: ApiService = RetrofitClient.api

    private val database: AppDatabase = AppDatabase.obtener(context)

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(api, database.itemDao())
    }

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
}