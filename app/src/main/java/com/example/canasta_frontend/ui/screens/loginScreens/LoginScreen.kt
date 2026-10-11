package com.example.canasta_frontend.ui.screens.loginScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.data.network.AuthRequest
import com.example.canasta_frontend.data.network.RegisterRequest
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.SecureTokenManager
import com.example.canasta_frontend.ui.theme.*
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isRegistering by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localidad by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val tokenManager = remember { SecureTokenManager(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C+",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                }

                Text(
                    text = if (isRegistering) "Crea tu cuenta en Canasta+" else "Bienvenido a Canasta+",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isRegistering) "Completa tus datos para registrarte" else "Ingresa tus credenciales para continuar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                if (isRegistering) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (isRegistering) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = description, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = localidad,
                        onValueChange = { localidad = it },
                        label = { Text("Localidad (Opcional)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || (isRegistering && nombre.isBlank())) {
                            errorMessage = "Por favor completa todos los campos requeridos."
                            return@Button
                        }
                        if (isRegistering && password != confirmPassword) {
                            errorMessage = "Las contraseñas no coinciden."
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null
                        val apiService = RetrofitClient.createService(context)
                        coroutineScope.launch {
                            try {
                                if (isRegistering) {
                                    val response = apiService.registrar(
                                        RegisterRequest(
                                            nombre = nombre.trim(),
                                            email = email.trim(),
                                            contrasena = password,
                                            localidad = localidad.ifBlank { null }
                                        )
                                    )
                                    val token = response.data?.token
                                    if (response.ok && !token.isNullOrBlank()) {
                                        tokenManager.saveToken(token)
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = response.error ?: response.mensaje ?: "Error al registrar usuario."
                                    }
                                } else {
                                    val response = apiService.login(
                                        AuthRequest(email = email.trim(), contrasena = password)
                                    )
                                    val token = response.data?.token
                                    if (response.ok && !token.isNullOrBlank()) {
                                        tokenManager.saveToken(token)
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = response.error ?: response.mensaje ?: "Credenciales incorrectas."
                                    }
                                }
                            } catch (e: HttpException) {
                                try {
                                    val errorBody = e.response()?.errorBody()?.string()
                                    if (!errorBody.isNullOrBlank()) {
                                        val gson = Gson()
                                        val map = gson.fromJson(errorBody, Map::class.java)
                                        val errStr = map["error"] as? String
                                        val detalles = map["detalles"] as? List<*>
                                        val detailMsg = detalles?.firstOrNull()?.let {
                                            (it as? Map<*, *>)?.get("message") as? String
                                        }
                                        errorMessage = detailMsg ?: errStr ?: "Error en la solicitud."
                                    } else {
                                        errorMessage = "Error en el servidor (${e.code()})"
                                    }
                                } catch (_: Exception) {
                                    errorMessage = "Error en la solicitud (${e.code()})"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error de conexión: ${e.localizedMessage ?: "Verifica tu conexión"}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isRegistering) "Registrarse" else "Iniciar Sesión",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                TextButton(
                    onClick = {
                        isRegistering = !isRegistering
                        errorMessage = null
                    }
                ) {
                    Text(
                        text = if (isRegistering) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate aquí",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    cursorColor = MaterialTheme.colorScheme.primary
)
