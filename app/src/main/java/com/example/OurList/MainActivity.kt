
package com.example.ourlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShoppingItem(
    val id: Int,
    val name: String,
    val quantity: String,
    val note: String,
    var isPurchased: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF2E5A44),
                    secondary = Color(0xFF4E7C59),
                    background = Color(0xFFF9F9F6),
                    surface = Color(0xFFFFFFFF)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShoppingListScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen() {
    var items by remember {
        mutableStateOf(
            listOf(
                ShoppingItem(1, "Milk", "2 litres", "Tomorrow morning"),
                ShoppingItem(2, "Rice", "5 kg", "Same brand as last time"),
                ShoppingItem(3, "Eggs", "12 pack", "Free-range"),
                ShoppingItem(4, "Tomatoes", "1 kg", "Ripe ones")
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newQuantity by remember { mutableStateOf("") }
    var newNote by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("OurList", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Family shopping list", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF2E5A44),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val toBuy = items.filter { !it.isPurchased }
            val purchased = items.filter { it.isPurchased }

            Text(
                text = "To buy (${toBuy.size})",
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isPurchased) Color(0xFFEFEFEF) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isPurchased,
                                onCheckedChange = { checked ->
                                    items = items.map {
                                        if (it.id == item.id) it.copy(isPurchased = checked) else it
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (item.isPurchased) Color.Gray else Color.Black
                                )
                                if (item.quantity.isNotEmpty()) {
                                    Text(text = item.quantity, fontSize = 13.sp, color = Color.DarkGray)
                                }
                                if (item.note.isNotEmpty()) {
                                    Text(text = item.note, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Item") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newItemName,
                            onValueChange = { newItemName = it },
                            label = { Text("Item Name") }
                        )
                        OutlinedTextField(
                            value = newQuantity,
                            onValueChange = { newQuantity = it },
                            label = { Text("Quantity (e.g. 2 kg)") }
                        )
                        OutlinedTextField(
                            value = newNote,
                            onValueChange = { newNote = it },
                            label = { Text("Note (optional)") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newItemName.isNotBlank()) {
                            val newEntry = ShoppingItem(
                                id = (items.maxOfOrNull { it.id } ?: 0) + 1,
                                name = newItemName,
                                quantity = newQuantity,
                                note = newNote
                            )
                            items = items + newEntry
                            newItemName = ""
                            newQuantity = ""
                            newNote = ""
                            showDialog = false
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
