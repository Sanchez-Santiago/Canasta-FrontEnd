package com.example.canasta_frontend.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopHeader(
    modoGrupo: String = "personal",
    onModoChange: (String) -> Unit = {},
    groupName: String = "Canasta Personal",
    userName: String = "Usuario",
    avatarUrl: String? = null,
    unreadCount: Int = 0,
    onGroupClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onInviteClick: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
    Surface(
        color = cs.surface,
        tonalElevation = 2.dp,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, cs.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(cs.primary.copy(alpha = 0.85f), cs.primaryContainer)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, cs.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "C+",
                            color = cs.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Canasta+",
                                color = cs.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(cs.primary.copy(alpha = 0.15f))
                                    .border(1.dp, cs.primary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (modoGrupo == "personal") "Personal" else "Grupal",
                                    color = cs.primary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Text(
                            text = groupName,
                            color = cs.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(cs.surfaceVariant)
                            .border(1.dp, cs.outline, CircleShape)
                            .clickable { onNotificationClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = cs.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(cs.error, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, cs.primary, CircleShape)
                            .background(cs.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userName.isNotEmpty()) userName.substring(0, 1).uppercase() else "U",
                            color = cs.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(cs.surfaceVariant, CircleShape)
                        .border(1.dp, cs.outline, CircleShape)
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (modoGrupo == "personal") cs.primary else cs.surfaceVariant)
                            .clickable { onModoChange("personal") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Individual",
                            tint = if (modoGrupo == "personal") cs.onPrimary else cs.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (modoGrupo == "grupal") cs.primary else cs.surfaceVariant)
                            .clickable { onModoChange("grupal") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Grupal",
                            tint = if (modoGrupo == "grupal") cs.onPrimary else cs.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (modoGrupo == "grupal") {
                    IconButton(
                        onClick = onGroupClick,
                        modifier = Modifier.size(44.dp).background(cs.surfaceVariant, CircleShape).border(1.dp, cs.outline, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = groupName, tint = cs.onBackground, modifier = Modifier.size(18.dp))
                    }

                    Button(
                        onClick = onInviteClick,
                        modifier = Modifier.size(44.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Invitar", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
    }
}
