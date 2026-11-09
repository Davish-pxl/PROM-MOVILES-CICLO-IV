package com.tuapp.valcarcelcomida

import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.data.remote.RetrofitClient
import com.tuapp.valcarcelcomida.data.repository.ItemRepository
import com.tuapp.valcarcelcomida.data.repository.ItemRepositoryImpl

class AppContainer {

    private val api: ApiService = RetrofitClient.api
    val itemRepository: ItemRepository by lazy { ItemRepositoryImpl(api) }
}