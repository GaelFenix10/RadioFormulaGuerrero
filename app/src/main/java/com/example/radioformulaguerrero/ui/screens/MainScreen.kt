package com.example.radioformulaguerrero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.radioformulaguerrero.R
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.radioformulaguerrero.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val viewModel: MainViewModel = viewModel()
    var selectedTab by remember { mutableStateOf("home") }
    var isLoggedIn by remember { mutableStateOf(false) }
    var showAccountMenu by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }

    val tabs = listOf(
        TabItem("Inicio", Icons.Default.Home, "home"),
        TabItem("Radio", Icons.Default.Radio, "radio"),
        TabItem("Programación", Icons.Default.Schedule, "schedule"),
        TabItem("Quejas", Icons.Default.Report, "complaints")
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo Radio Formula Guerrero",
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                        )
                    },
                    actions = {
                        IconButton(onClick = { showAccountMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = if (isLoggedIn) "Cuenta activa" else "Iniciar sesión"
                            )
                        }
                        DropdownMenu(
                            expanded = showAccountMenu,
                            onDismissRequest = { showAccountMenu = false }
                        ) {
                            if (!isLoggedIn) {
                                DropdownMenuItem(
                                    text = { Text("Iniciar sesión") },
                                    onClick = {
                                        showAccountMenu = false
                                        navController.navigate("login")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Crear cuenta") },
                                    onClick = {
                                        showAccountMenu = false
                                        navController.navigate("register")
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Cerrar sesión") },
                                    onClick = {
                                        isLoggedIn = false
                                        userEmail = ""
                                        showAccountMenu = false
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color(0xFF1A1A1A)
                    ),
                    modifier = Modifier.statusBarsPadding()
                )
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                tab.icon, 
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedTab == tab.route) 
                                        FontWeight.Bold 
                                    else 
                                        FontWeight.Normal
                                )
                            ) 
                        },
                        selected = selectedTab == tab.route,
                        onClick = {
                            selectedTab = tab.route
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                "home" -> HomeScreen(viewModel)
                "radio" -> RadioScreen(navController)
                "schedule" -> ScheduleScreen(navController, viewModel)
                "complaints" -> ComplaintsScreen(navController, viewModel)
                else -> HomeScreen(viewModel)
            }
        }
    }
}

private data class TabItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) 