package com.wealthflow.app.presentation.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthflow.app.data.local.dao.CategoryTotal
import java.text.NumberFormat
import java.util.Locale

private fun aed(amount: Double) = "AED " + NumberFormat.getNumberInstance(Locale.US).apply {
    minimumFractionDigits = 2; maximumFractionDigits = 2
}.format(amount)

@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Reports & Analytics", style = MaterialTheme.typography.headlineMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Total Income", aed(state.totalIncome), Modifier.weight(1f))
            SummaryCard("Total Expenses", aed(state.totalExpense), Modifier.weight(1f))
        }

        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(16.dp)
        ) {
            Column {
                Text("Category Spending", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PieChart(state.categoryTotals, Modifier.size(180.dp))
                }
                Spacer(Modifier.height(16.dp))
                state.categoryTotals.forEach { ct -> CategoryLegendRow(ct) }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(16.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PieChart(data: List<CategoryTotal>, modifier: Modifier = Modifier) {
    val total = data.sumOf { it.total }.takeIf { it > 0 } ?: 1.0
    Canvas(modifier = modifier) {
        var startAngle = -90f
        data.forEach { ct ->
            val sweep = (ct.total / total * 360f).toFloat()
            drawArc(
                color = runCatching { Color(android.graphics.Color.parseColor(ct.color)) }.getOrDefault(Color.Gray),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun CategoryLegendRow(ct: CategoryTotal) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(12.dp).clip(CircleShape)
                    .background(runCatching { Color(android.graphics.Color.parseColor(ct.color)) }.getOrDefault(Color.Gray))
            )
            Spacer(Modifier.width(8.dp))
            Text(ct.categoryName, style = MaterialTheme.typography.bodyMedium)
        }
        Text(aed(ct.total), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
