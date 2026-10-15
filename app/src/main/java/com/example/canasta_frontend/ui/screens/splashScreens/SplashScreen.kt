package com.example.canasta_frontend.ui.screens.splashScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.SecureTokenManager
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    var isChecking by remember { mutableStateOf(true) }
    var connectionError by remember { mutableStateOf<String?>(null) }
    var retryTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(retryTrigger) {
        isChecking = true
        connectionError = null
        delay(600)

        val tokenManager = SecureTokenManager(context)
        val token = tokenManager.getToken()
        val api = RetrofitClient.createService(context)

        if (!token.isNullOrEmpty()) {
            try {
                val response = api.getYo()
                if (response.ok && response.data?.usuario != null) {
                    onNavigateToHome()
                } else {
                    tokenManager.clearToken()
                    onNavigateToLogin()
                }
            } catch (e: HttpException) {
                if (e.code() == 401 || e.code() == 403) {
                    tokenManager.clearToken()
                    onNavigateToLogin()
                } else {
                    isChecking = false
                    connectionError = "No se pudo conectar con el servidor (error ${e.code()}). Reintentá más tarde."
                }
            } catch (_: IOException) {
                isChecking = false
                connectionError = "No se pudo conectar con el servidor. Verificá tu conexión a internet e intentá más tarde."
            } catch (_: Exception) {
                isChecking = false
                connectionError = "No se pudo establecer conexión con el servidor. Intentá más tarde."
            }
        } else {
            try {
                api.getOfertas()
                onNavigateToLogin()
            } catch (_: IOException) {
                isChecking = false
                connectionError = "No se pudo conectar con el servidor. Verificá tu conexión a internet e intentá más tarde."
            } catch (_: Exception) {
                onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C+",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )
            }
            Text(
                text = "Canasta+",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gestión Inteligente y Colaborativa",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isChecking) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.5.dp
                )
            } else if (connectionError != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Error de Conexión",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = connectionError!!,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { retryTrigger++ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reintentar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reintentar conexión", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
