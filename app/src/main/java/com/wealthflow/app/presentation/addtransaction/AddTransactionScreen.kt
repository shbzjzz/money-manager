package com.wealthflow.app.presentation.addtransaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthflow.app.data.local.entity.TxType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onClose: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showCategorySheet by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = { viewModel.save(onClose) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Save Transaction", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold) }
            }
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Segmented type selector
            SingleChoiceSegment(state.type, onSelect = viewModel::onTypeChange)

            // Amount input
            Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Amount", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("AED ", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    BasicAmountField(state.amountText, viewModel::onAmountChange)
                }
            }

            SelectorRow(
                icon = Icons.Filled.Restaurant,
                label = "Category",
                value = state.selectedCategory?.name ?: "Select category",
                onClick = { showCategorySheet = true }
            )
            SelectorRow(
                icon = Icons.Filled.AccountBalance,
                label = "Account",
                value = state.selectedAccount?.name ?: "Select account",
                onClick = { showAccountSheet = true }
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
    }

    if (showCategorySheet) {
        ModalBottomSheet(onDismissRequest = { showCategorySheet = false }) {
            LazyColumnPicker(state.categories.map { it.name to it }) { picked ->
                viewModel.onCategorySelect(picked); showCategorySheet = false
            }
        }
    }
    if (showAccountSheet) {
        ModalBottomSheet(onDismissRequest = { showAccountSheet = false }) {
            LazyColumnPicker(state.accounts.map { it.name to it }) { picked ->
                viewModel.onAccountSelect(picked); showAccountSheet = false
            }
        }
    }
}

@Composable
private fun SingleChoiceSegment(selected: TxType, onSelect: (TxType) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(4.dp)
    ) {
        TxType.values().forEach { t ->
            val isSelected = t == selected
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) MaterialTheme.colorScheme.surface else androidx.compose.ui.graphics.Color.Transparent)
                    .clickableNoRipple { onSelect(t) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    t.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun BasicAmountField(value: String, onChange: (String) -> Unit) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = { if (it.length < 12) onChange(it) },
        textStyle = MaterialTheme.typography.displayLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.width(160.dp)
    )
}

@Composable
private fun SelectorRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickableNoRipple(onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primaryContainer) }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun <T> LazyColumnPicker(items: List<Pair<String, T>>, onPick: (T) -> Unit) {
    LazyColumn(Modifier.padding(16.dp).fillMaxWidth()) {
        items(items) { (label, value) ->
            Text(
                label,
                modifier = Modifier.fillMaxWidth().clickableNoRipple { onPick(value) }.padding(vertical = 14.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = onClick
)
