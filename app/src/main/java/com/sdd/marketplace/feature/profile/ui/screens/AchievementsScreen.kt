package com.sdd.marketplace.feature.profile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.theme.*

data class AchievementItem(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val date: String,
    val isUnlocked: Boolean,
    val progress: Float = 1f,
    val progressText: String = "",
    val category: String = "General"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                title = { Text("Achievements", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SddPink.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 48.sp)
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "Achievements",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Coming Soon",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = SddPink
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Your badges, milestones, and seller achievements will appear here. Keep selling, earning great reviews, and building your reputation!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.height(32.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SddPink.copy(alpha = 0.06f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Upcoming Achievement Types",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = SddPink
                        )
                        Spacer(Modifier.height(12.dp))
                        listOf(
                            "🛍️" to "Sales milestones",
                            "⭐" to "Rating achievements",
                            "🤝" to "Community badges",
                            "🚀" to "Listing milestones",
                            "🌟" to "Trust & verification rewards"
                        ).forEach { (emoji, label) ->
                            Row(
                                Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(emoji, fontSize = 18.sp)
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
