package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinControlApp(viewModel: FinanceViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BoxWithConstraints {
        val isWideScreen = maxWidth > 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (isWideScreen) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        if (currentRoute == "dashboard" || currentRoute == "transactions") {
                            FloatingActionButton(
                                onClick = { navController.navigate("add_transaction") },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Adicionar Transação")
                            }
                        }
                    }
                ) {
                    NavigationRailItem(
                        icon = { Icon(Icons.Rounded.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Resumo") },
                        selected = currentRoute == "dashboard",
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationRailItem(
                        icon = { Icon(Icons.Rounded.ListAlt, contentDescription = "Transações") },
                        label = { Text("Transações") },
                        selected = currentRoute == "transactions",
                        onClick = {
                            navController.navigate("transactions") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationRailItem(
                        icon = { Icon(Icons.Rounded.AutoGraph, contentDescription = "IA Assistente") },
                        label = { Text("IA") },
                        selected = currentRoute == "ai_assistant",
                        onClick = {
                            navController.navigate("ai_assistant") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = { Text("FinControl", style = MaterialTheme.typography.titleLarge) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                },
                bottomBar = {
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Rounded.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("Resumo") },
                                selected = currentRoute == "dashboard",
                                onClick = {
                                    navController.navigate("dashboard") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Rounded.ListAlt, contentDescription = "Transações") },
                                label = { Text("Transações") },
                                selected = currentRoute == "transactions",
                                onClick = {
                                    navController.navigate("transactions") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Rounded.AutoGraph, contentDescription = "IA Assistente") },
                                label = { Text("IA") },
                                selected = currentRoute == "ai_assistant",
                                onClick = {
                                    navController.navigate("ai_assistant") {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (!isWideScreen && (currentRoute == "dashboard" || currentRoute == "transactions")) {
                        FloatingActionButton(
                            onClick = { navController.navigate("add_transaction") },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Adicionar Transação")
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(modifier = Modifier.widthIn(max = 800.dp).fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = "dashboard",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("dashboard") { DashboardScreen(viewModel) }
                            composable("transactions") { TransactionsScreen(viewModel) }
                            composable("add_transaction") { AddTransactionScreen(viewModel, onNavigateBack = { navController.popBackStack() }) }
                            composable("ai_assistant") { AiAssistantScreen(viewModel) }
                        }
                    }
                }
            }
        }
    }
}
