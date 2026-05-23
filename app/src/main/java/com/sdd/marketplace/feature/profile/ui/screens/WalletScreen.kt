package com.sdd.marketplace.feature.profile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.theme.SddPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                title = { Text("My Wallet", fontWeight = FontWeight.Bold) }
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
                        .background(
                            Brush.radialGradient(
                                listOf(SddPink.copy(alpha = 0.2f), SddPink.copy(alpha = 0.05f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AccountBalanceWallet,
                        "Wallet",
                        tint = SddPink,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "Wallet",
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
                    "We're working hard to bring you a seamless in-app wallet experience. Stay tuned for balance management, withdrawals, and instant payouts.",
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
                            "Planned Features",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = SddPink
                        )
                        Spacer(Modifier.height(12.dp))
                        listOf(
                            Icons.Filled.AccountBalance to "Bank account withdrawals",
                            Icons.Filled.CreditCard to "Add money via card or UPI",
                            Icons.Filled.History to "Full transaction history",
                            Icons.Filled.SwapHoriz to "Instant peer-to-peer transfers",
                            Icons.Filled.Shield to "Buyer & seller protection"
                        ).forEach { (icon, label) ->
                            Row(
                                Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    icon, null,
                                    tint = SddPink,
                                    modifier = Modifier.size(18.dp)
                                )
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
