package com.example.zerowaste

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

data class FoodItem(val name: String, val expiryDate: String)

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
            composable("home") { HomeScreen(navController) }
            composable("scan") { ScanScreen() }
            composable("order") { OrderScreen() }
            composable("recipes") { RecipesScreen() }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val expiringFoods = listOf(
        FoodItem("Milk", "Expires in 2 days"),
        FoodItem("Bread", "Expires in 3 days"),
        FoodItem("Chicken", "Expires tomorrow")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "ZeroWaste", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Welcome back!", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.navigate("scan") }) {
                Text("Scan Food")
            }
            Button(onClick = { navController.navigate("order") }) {
                Text("Order Foods")
            }
            Button(onClick = { navController.navigate("recipes") }) {
                Text("Create Recipe")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Expiring Soon", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(expiringFoods) { food ->
                FoodListItem(food)
            }
        }
    }
}

@Composable
fun FoodListItem(food: FoodItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = food.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = food.expiryDate, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }
    }
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
