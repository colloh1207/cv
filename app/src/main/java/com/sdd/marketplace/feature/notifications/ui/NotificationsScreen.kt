package com.sdd.marketplace.feature.notifications.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.components.EmptyState
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.domain.model.Notification
import com.sdd.marketplace.domain.model.NotificationType
import com.sdd.marketplace.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    val uiState = MutableStateFlow(NotificationsUiState())

    init {
        viewModelScope.launch {
            notificationRepository.getNotifications().collect { notifications ->
                uiState.update { it.copy(notifications = notifications, isLoading = false) }
            }
        }
    }

    fun markRead(id: String) = viewModelScope.launch { notificationRepository.markRead(id) }
    fun markAllRead() = viewModelScope.launch { notificationRepository.markAllRead() }
}

@Composable
fun NotificationsScreen(navController: NavController, viewModel: NotificationsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            title = { Text("Notifications", fontWeight = FontWeight.Bold) },
            actions = {
                TextButton(onClick = { viewModel.markAllRead() }) { Text("Mark all read", color = SddPink) }
            }
        )

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = SddPink) }
        } else if (uiState.notifications.isEmpty()) {
            EmptyState("No notifications", "You're all caught up!", icon = { Icon(Icons.Outlined.Notifications, "Empty", modifier = Modifier.size(64.dp)) })
        } else {
            LazyColumn {
                items(uiState.notifications, key = { it.id }) { notification ->
                    NotificationItem(notification = notification, onClick = { viewModel.markRead(notification.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

private val URL_PATTERN = Regex("""https?://[^\s]+""")

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    val context = LocalContext.current
    val isAdminOrSystem = notification.type == NotificationType.SYSTEM

    val annotatedBody = remember(notification.body) {
        buildAnnotatedString {
            var lastEnd = 0
            URL_PATTERN.findAll(notification.body).forEach { match ->
                append(notification.body.substring(lastEnd, match.range.first))
                pushStringAnnotation("URL", match.value)
                withStyle(SpanStyle(color = SddPink, textDecoration = TextDecoration.Underline)) {
                    append(match.value)
                }
                pop()
                lastEnd = match.range.last + 1
            }
            if (lastEnd < notification.body.length) {
                append(notification.body.substring(lastEnd))
            }
        }
    }

    ListItem(
        headlineContent = {
            Text(
                notification.title,
                fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal
            )
        },
        supportingContent = {
            if (URL_PATTERN.containsMatchIn(notification.body)) {
                androidx.compose.foundation.text.ClickableText(
                    text = annotatedBody,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = { offset ->
                        annotatedBody.getStringAnnotations("URL", offset, offset)
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                    }
                )
            } else {
                Text(notification.body, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        leadingContent = {
            val (icon, tint) = when (notification.type) {
                NotificationType.MESSAGE -> Icons.Filled.Message to SddPink
                NotificationType.LIKE -> Icons.Filled.Favorite to androidx.compose.ui.graphics.Color(0xFFE91E63)
                NotificationType.FOLLOW -> Icons.Filled.PersonAdd to SddPink
                NotificationType.SALE -> Icons.Filled.ShoppingBag to androidx.compose.ui.graphics.Color(0xFF4CAF50)
                NotificationType.OFFER -> Icons.Filled.LocalOffer to androidx.compose.ui.graphics.Color(0xFFFF9800)
                NotificationType.ORDER_UPDATE -> Icons.Filled.LocalShipping to SddPink
                NotificationType.REVIEW, NotificationType.REVIEW_REPLY -> Icons.Filled.StarRate to androidx.compose.ui.graphics.Color(0xFFFFC107)
                NotificationType.KYC_UPDATE -> Icons.Filled.VerifiedUser to SddPink
                NotificationType.PAYMENT -> Icons.Filled.Payment to androidx.compose.ui.graphics.Color(0xFF4CAF50)
                NotificationType.SYSTEM -> Icons.Filled.Campaign to SddPink
                else -> Icons.Filled.Notifications to SddPink
            }
            Icon(icon, notification.type.name, tint = tint)
        },
        trailingContent = {
            if (!notification.isRead) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(SddPink, CircleShape)
                )
            }
        },
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (!notification.isRead) SddPink.copy(alpha = 0.04f)
                else MaterialTheme.colorScheme.surface
            )
    )
}
