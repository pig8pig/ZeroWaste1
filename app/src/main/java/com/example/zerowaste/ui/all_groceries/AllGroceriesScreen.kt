package com.example.zerowaste.ui.all_groceries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zerowaste.data.model.Grocery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGroceriesScreen(viewModel: AllGroceriesViewModel = hiltViewModel()) {
    val groceries by viewModel.groceries.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val search by viewModel.search.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "All Groceries", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.setSearch(it) },
            label = { Text("Search Groceries") },
            modifier = Modifier
                .fillMaxWidth()
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterButton(
                    text = "All",
                    selected = filter is GroceryFilter.All,
                    onClick = { viewModel.setFilter(GroceryFilter.All) }
                )
            }
            item {
                FilterButton(
                    text = "Today",
                    selected = filter is GroceryFilter.ExpiringToday,
                    onClick = { viewModel.setFilter(GroceryFilter.ExpiringToday) }
                )
            }
            item {
                FilterButton(
                    text = "Tomorrow",
                    selected = filter is GroceryFilter.ExpiringTomorrow,
                    onClick = { viewModel.setFilter(GroceryFilter.ExpiringTomorrow) }
                )
            }
            item {
                FilterButton(
                    text = "Fruit",
                    selected = (filter is GroceryFilter.Type && (filter as GroceryFilter.Type).type == "Fruit"),
                    onClick = { viewModel.setFilter(GroceryFilter.Type("Fruit")) }
                )
            }
            item {
                FilterButton(
                    text = "Vegetable",
                    selected = (filter is GroceryFilter.Type && (filter as GroceryFilter.Type).type == "Vegetable"),
                    onClick = { viewModel.setFilter(GroceryFilter.Type("Vegetable")) }
                )
            }
            item {
                FilterButton(
                    text = "Meat",
                    selected = (filter is GroceryFilter.Type && (filter as GroceryFilter.Type).type == "Meat"),
                    onClick = { viewModel.setFilter(GroceryFilter.Type("Meat")) }
                )
            }
            item {
                FilterButton(
                    text = "Dairy",
                    selected = (filter is GroceryFilter.Type && (filter as GroceryFilter.Type).type == "Dairy"),
                    onClick = { viewModel.setFilter(GroceryFilter.Type("Dairy")) }
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(groceries) { grocery ->
                GroceryItem(grocery = grocery)
            }
        }
    }
}

@Composable
private fun FilterButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val container = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val content = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content
        )
    ) {
        Text(text)
    }
}

@Composable
fun GroceryItem(grocery: Grocery) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = grocery.name, modifier = Modifier.weight(1f))
        Text(text = "${grocery.quantity} ${grocery.units}")
        if (grocery.daysToExpiry < 0) {
            Text(
                text = "Expired ${grocery.daysToExpiry * -1} days ago",
                modifier = Modifier.padding(start = 16.dp)
            )
        } else if (grocery.daysToExpiry == 0)  {
            Text(
                text = "Expires Today!",
                modifier = Modifier.padding(start = 16.dp)
            )
        } else {
            Text(
                text = "Expires in ${grocery.daysToExpiry} days",
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}