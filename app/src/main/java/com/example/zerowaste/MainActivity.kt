package com.example.zerowaste

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Scanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zerowaste.data.model.Grocery
import com.example.zerowaste.ui.all_groceries.AllGroceriesScreen
import com.example.zerowaste.ui.home.HomeViewModel
import com.example.zerowaste.ui.recipe.RecipeScreen
import com.example.zerowaste.ui.scan.ScanScreen
import com.example.zerowaste.ui.theme.ZeroWasteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                BottomNavigationItem(icon = {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                }, onClick = { navController.navigate("home") })
                BottomNavigationItem(icon = {
                    Icon(Icons.Filled.List, contentDescription = "All Groceries")
                }, onClick = { navController.navigate("all-groceries") })
                BottomNavigationItem(icon = {
                    Icon(Icons.Filled.Scanner, contentDescription = "Scan")
                }, onClick = { navController.navigate("scan/in") })
                BottomNavigationItem(icon = {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Order")
                }, onClick = { navController.navigate("order") })
                BottomNavigationItem(icon = {
                    Icon(Icons.Filled.RestaurantMenu, contentDescription = "Recipes")
                }, onClick = { navController.navigate("recipes") })
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen(navController) }
            composable("all-groceries") { AllGroceriesScreen() }
            composable("scan/{type}") { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: "in"
                ScanScreen(
                    type = type,
                    onConfirm = { navController.navigate("home") }
                )
            }
            composable("order") { OrderScreen() }
            composable("recipes") { RecipeScreen() }
        }
    }
}

@Composable
fun RowScope.BottomNavigationItem(icon: @Composable () -> Unit, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.weight(1f)
    ) {
        icon()
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val expiringSoon by viewModel.expiringSoon.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.advanceDay() }) {
                Icon(Icons.Default.Add, contentDescription = "Advance Day")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ZeroWaste", style = MaterialTheme.typography.headlineLarge)
                Icon(Icons.Default.AccountCircle, contentDescription = "Account", modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2x2 Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Scanner,
                    text = "Food In",
                    onClick = { navController.navigate("scan/in") })
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Scanner,
                    text = "Food Out",
                    onClick = { navController.navigate("scan/out") })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ShoppingCart,
                    text = "Order Food",
                    onClick = { navController.navigate("order") })
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.RestaurantMenu,
                    text = "Create Recipe",
                    onClick = { navController.navigate("recipes") })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Expiring Soon", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(expiringSoon) { grocery ->
                    FoodListItem(grocery)
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(2.5f)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun FoodListItem(grocery: Grocery) {
    val expiryText = when {
        grocery.daysToExpiry < 0 -> "Expired"
        grocery.daysToExpiry == 0 -> "Expires today"
        grocery.daysToExpiry == 1 -> "Expires tomorrow"
        else -> "Expires in ${grocery.daysToExpiry} days"
    }

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
            Text(text = grocery.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = expiryText, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun OrderScreen() {
    Text(text = "Order Screen")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ZeroWasteTheme {
        //MainScreen()
    }
}
