package com.tuapp.tecsupstore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    content: @Composable () -> Unit
) {
    val purplePrimary = Color(0xFF4A148C)
    val purpleLightBg = Color(0xFFECE6F0)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8DEF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "DV",
                                fontWeight = FontWeight.Bold,
                                color = purplePrimary,
                                fontSize = 16.sp
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
                            Text(
                                text = "david@tecsup.edu.pe",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.8.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    DrawerMenuItemCustom(
                        label = "Inicio",
                        isSelected = currentRoute == Screen.Home.route,
                        purplePrimary = purplePrimary,
                        purpleLightBg = purpleLightBg,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Home.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerMenuItemCustom(
                        label = "Mis pedidos",
                        isSelected = currentRoute == Screen.List.route || currentRoute == null,
                        purplePrimary = purplePrimary,
                        purpleLightBg = purpleLightBg,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.List.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerMenuItemCustom(
                        label = "Favoritos",
                        isSelected = false,
                        purplePrimary = purplePrimary,
                        purpleLightBg = purpleLightBg,
                        onClick = {
                            scope.launch { drawerState.close() }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerMenuItemCustom(
                        label = "Perfil",
                        isSelected = currentRoute == Screen.Profile.route,
                        purplePrimary = purplePrimary,
                        purpleLightBg = purpleLightBg,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Profile.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawerMenuItemCustom(
                        label = "Cerrar sesion",
                        isSelected = false,
                        purplePrimary = purplePrimary,
                        purpleLightBg = purpleLightBg,
                        onClick = {
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        content()
    }
}

@Composable
private fun DrawerMenuItemCustom(
    label: String,
    isSelected: Boolean,
    purplePrimary: Color,
    purpleLightBg: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) purpleLightBg else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) purplePrimary else Color.DarkGray,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) purplePrimary else Color.DarkGray,
                fontSize = 15.sp
            )
        }
    }
}