package com.tuapp.tecsupstore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tuapp.tecsupstore.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavController,
    currentRoute: String?,
    favoritosCount: Int = 0,
    content: @Composable () -> Unit
) {
    val purplePrimary = Color(0xFF4A148C)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Encabezado del usuario
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8DEF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "DV",
                                fontWeight = FontWeight.Bold,
                                color = purplePrimary,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "David Valcarcel",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "david@tecsup.edu.pe",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    // Opciones de navegación
                    NavigationDrawerItem(
                        label = { Text("Inicio", fontWeight = FontWeight.SemiBold) },
                        selected = currentRoute == Screen.Home.route,
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFECE6F0),
                            selectedIconColor = purplePrimary,
                            selectedTextColor = purplePrimary
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Mis pedidos", fontWeight = FontWeight.SemiBold) },
                        selected = currentRoute == Screen.List.route,
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Mis pedidos") },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFECE6F0),
                            selectedIconColor = purplePrimary,
                            selectedTextColor = purplePrimary
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.List.route) {
                                navController.navigate(Screen.List.route)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Favoritos", fontWeight = FontWeight.SemiBold) },
                        selected = currentRoute == Screen.Favoritos.route,
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                        badge = {
                            if (favoritosCount > 0) {
                                Badge(
                                    containerColor = purplePrimary,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = favoritosCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFECE6F0),
                            selectedIconColor = purplePrimary,
                            selectedTextColor = purplePrimary
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.Favoritos.route) {
                                navController.navigate(Screen.Favoritos.route)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Perfil", fontWeight = FontWeight.SemiBold) },
                        selected = currentRoute == Screen.Profile.route,
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFECE6F0),
                            selectedIconColor = purplePrimary,
                            selectedTextColor = purplePrimary
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != Screen.Profile.route) {
                                navController.navigate(Screen.Profile.route)
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión", fontWeight = FontWeight.SemiBold) },
                        selected = false,
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión") },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedIconColor = Color.Red,
                            unselectedTextColor = Color.Red
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        content()
    }
}