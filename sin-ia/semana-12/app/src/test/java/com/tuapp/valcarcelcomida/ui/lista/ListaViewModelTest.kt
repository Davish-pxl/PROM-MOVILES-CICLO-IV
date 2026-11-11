package com.tuapp.valcarcelcomida.ui.lista

import com.tuapp.valcarcelcomida.data.auth.AuthRepository
import com.tuapp.valcarcelcomida.data.repository.ItemRepository
import com.tuapp.valcarcelcomida.domain.model.Item
import com.tuapp.valcarcelcomida.domain.model.Rol
import com.tuapp.valcarcelcomida.domain.model.Sesion
import com.tuapp.valcarcelcomida.notification.Notificador
import com.tuapp.valcarcelcomida.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

private class FakeItemRepository(
    guardados: List<Item> = emptyList(),
    private val resultadoRefrescar: Result<Int>,
    private val traidosDeLaApi: List<Item> = emptyList()
) : ItemRepository {

    private val datos = MutableStateFlow(guardados)

    override val items: Flow<List<Item>> = datos
    override val favoritos: Flow<List<Item>> = datos.map { lista -> lista.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> {
        resultadoRefrescar.onSuccess { datos.value = traidosDeLaApi }
        return resultadoRefrescar
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> =
        datos.value.firstOrNull { it.id == id }?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException())

    override suspend fun alternarFavorito(id: String) {
        datos.value = datos.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
    }

    override suspend fun cantidadGuardada(): Int = datos.value.size
    override suspend fun cantidadFavoritos(): Int = datos.value.count { it.esFavorito }
    override suspend fun limpiarCache() { datos.value = emptyList() }
}

private class FakeAuthRepository(private val rol: Rol = Rol.USUARIO) : AuthRepository {
    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> =
        Result.success(Sesion("uid-1", correo, rol))
    override suspend fun sesionActual(): Sesion = Sesion("uid-1", "prueba@correo.com", rol)
    override fun cerrarSesion() {}
}

private class FakeNotificador : Notificador {
    val mostradas = mutableListOf<Pair<String, String>>()
    override fun mostrar(titulo: String, texto: String) {
        mostradas += titulo to texto
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    private val pollo1 = Item(id = "1", titulo = "Pollo al horno", subtitulo = "Chicken", imagenUrl = null)
    private val pollo2 = Item(id = "2", titulo = "Pollo a la brasa", subtitulo = "Chicken", imagenUrl = null)

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refrescar con exito muestra la lista y lanza la notificacion`() = runTest {
        val repo = FakeItemRepository(
            resultadoRefrescar = Result.success(2),
            traidosDeLaApi = listOf(pollo1, pollo2)
        )
        val notificador = FakeNotificador()

        val vm = ListaViewModel(repo, FakeAuthRepository(), notificador)

        assertEquals(UiState.Exito(listOf(pollo1, pollo2)), vm.estado.value)
        assertNull(vm.aviso.value)
        assertEquals(1, notificador.mostradas.size)
        assertEquals("Se guardaron 2 elementos", notificador.mostradas.first().second)
    }

    @Test
    fun `sin internet y sin datos guardados muestra Error`() = runTest {
        val repo = FakeItemRepository(
            guardados = emptyList(),
            resultadoRefrescar = Result.failure(IOException())
        )
        val notificador = FakeNotificador()

        val vm = ListaViewModel(repo, FakeAuthRepository(), notificador)

        assertEquals(UiState.Error("No hay conexión a internet"), vm.estado.value)
        assertTrue("No debe notificar si falló", notificador.mostradas.isEmpty())
    }

    @Test
    fun `sin internet con datos guardados muestra la lista y el aviso`() = runTest {
        val repo = FakeItemRepository(
            guardados = listOf(pollo1),
            resultadoRefrescar = Result.failure(IOException())
        )

        val vm = ListaViewModel(repo, FakeAuthRepository(), FakeNotificador())

        assertEquals(UiState.Exito(listOf(pollo1)), vm.estado.value)
        assertEquals("Sin conexión: mostrando datos guardados", vm.aviso.value)
    }

    @Test
    fun `la API responde vacio y se muestra el estado vacio`() = runTest {
        val repo = FakeItemRepository(
            resultadoRefrescar = Result.success(0),
            traidosDeLaApi = emptyList()
        )

        val vm = ListaViewModel(repo, FakeAuthRepository(), FakeNotificador())

        assertEquals(UiState.Exito(emptyList<Item>()), vm.estado.value)
    }

    @Test
    fun `alternar favorito actualiza la lista`() = runTest {
        val repo = FakeItemRepository(
            resultadoRefrescar = Result.success(1),
            traidosDeLaApi = listOf(pollo1)
        )
        val vm = ListaViewModel(repo, FakeAuthRepository(), FakeNotificador())

        vm.alternarFavorito("1")

        val estado = vm.estado.value as UiState.Exito
        assertTrue(estado.datos.first().esFavorito)
    }

    @Test
    fun `el admin ve el acceso al panel y el usuario no`() = runTest {
        val repo = FakeItemRepository(resultadoRefrescar = Result.success(0))

        val vmAdmin = ListaViewModel(repo, FakeAuthRepository(Rol.ADMIN), FakeNotificador())
        val vmUsuario = ListaViewModel(repo, FakeAuthRepository(Rol.USUARIO), FakeNotificador())

        assertTrue(vmAdmin.esAdmin.value)
        assertEquals(false, vmUsuario.esAdmin.value)
    }
}