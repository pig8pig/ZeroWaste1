package com.example.zerowaste

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zerowaste.ui.theme.ZeroWasteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeroWasteTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                }
                IconButton(onClick = { navController.navigate("scan") }) {
                    Icon(Icons.Filled.Scanner, contentDescription = "Scan")
                }
                IconButton(onClick = { navController.navigate("order") }) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Order")
                }
                IconButton(onClick = { navController.navigate("recipes") }) {
                    Icon(Icons.Filled.RestaurantMenu, contentDescription = "Recipes")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add food */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Add food")
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen() }
            composable("scan") { ScanScreen() }
            composable("order") { OrderScreen() }
            composable("recipes") { RecipesScreen() }
        }
    }
}

@Composable
fun HomeScreen() {
    Text(text = "Home Screen")
}

@Composable
fun ScanScreen() {
    Text(text = "Scan Screen")
}

@Composable
fun OrderScreen() {
    Text(text = "Order Screen")
}

@Composable
fun RecipesScreen() {
    Text(text = "Recipes Screen")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ZeroWasteTheme {
        MainScreen()
    }
}
