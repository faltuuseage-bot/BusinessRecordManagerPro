package com.techfix.businessrecordmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = RecordDatabase.get(applicationContext)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RecordHome(dao = db.records())
                }
            }
        }
    }
}

private val categories = listOf(
    "Company", "Client", "Service", "Job", "Invoice", "Payment", "Expense", "Other"
)

@Composable
private fun RecordHome(dao: BusinessRecordDao) {
    val records by dao.observeAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showAdd by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }

    val visible = if (selectedCategory == "All") records else records.filter {
        it.category == selectedCategory
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Business Record Manager Pro",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text("Offline starter • PKR • Unlimited practical record creation")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showAdd = true }) { Text("Add record") }
            TextButton(onClick = { selectedCategory = "All" }) { Text("All (${records.size})") }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${record.category}: ${record.title}", fontWeight = FontWeight.SemiBold)
                        if (record.details.isNotBlank()) Text(record.details)
                        if (record.amountPaisa != 0L) {
                            Text("PKR ${"%.2f".format(record.amountPaisa / 100.0)}")
                        }
                        TextButton(onClick = {
                            scope.launch { dao.deleteById(record.id) }
                        }) { Text("Delete record") }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddRecordDialog(
            onDismiss = { showAdd = false },
            onSave = { category, title, details, amount ->
                scope.launch {
                    dao.insert(
                        BusinessRecord(
                            id = UUID.randomUUID().toString(),
                            category = category,
                            title = title.trim(),
                            details = details.trim(),
                            amountPaisa = (amount.toBigDecimalOrNull()?.movePointRight(2)
                                ?.setScale(0, java.math.RoundingMode.HALF_UP)
                                ?.longValueExact()) ?: 0L
                        )
                    )
                    showAdd = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var category by remember { mutableStateOf("Client") }
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val canSave = title.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add business record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Record type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { category = option; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Name / title *") }, singleLine = true
                )
                OutlinedTextField(
                    value = details, onValueChange = { details = it },
                    label = { Text("Details / notes") }
                )
                OutlinedTextField(
                    value = amount, onValueChange = { amount = it },
                    label = { Text("Amount in PKR (optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                Text("This starter stores records on this device only.")
            }
        },
        confirmButton = {
            Button(enabled = canSave, onClick = { onSave(category, title, details, amount) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
