package com.kevin.feedbackjuvenill

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen(
    viewModel: MainViewModel,
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Masculino") }
    var country by remember { mutableStateOf("Moçambique") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var genderExpanded by remember { mutableStateOf(false) }
    val genders = listOf("Masculino", "Feminino", "Outro")
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val backgroundColor = Color(0xFFF3F8FF)
    val cardColor = Color.White
    val accentColor = Color(0xFF1E88E5)
    val accentDark = Color(0xFF1565C0)
    val titleColor = Color(0xFF12355B)
    val subtitleColor = Color(0xFF60758C)
    val fieldBackground = Color(0xFFF8FBFF)
    val fieldBorder = Color(0xFFD9E7F8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .shadow(12.dp, CircleShape)
                    .background(cardColor, CircleShape)
                    .border(2.dp, accentColor.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .border(2.dp, accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FJ",
                        color = accentColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Create account",
                color = titleColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Join the voice of the youth",
                color = subtitleColor,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = accentColor) },
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = titleColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBackground,
                    unfocusedContainerColor = fieldBackground,
                    disabledContainerColor = fieldBackground,
                    errorContainerColor = fieldBackground,
                    focusedTextColor = titleColor,
                    unfocusedTextColor = titleColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = fieldBorder,
                    focusedLabelColor = subtitleColor,
                    unfocusedLabelColor = subtitleColor,
                    cursorColor = accentColor,
                    focusedPlaceholderColor = subtitleColor,
                    unfocusedPlaceholderColor = subtitleColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = accentColor) },
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = titleColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBackground,
                    unfocusedContainerColor = fieldBackground,
                    disabledContainerColor = fieldBackground,
                    errorContainerColor = fieldBackground,
                    focusedTextColor = titleColor,
                    unfocusedTextColor = titleColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = fieldBorder,
                    focusedLabelColor = subtitleColor,
                    unfocusedLabelColor = subtitleColor,
                    cursorColor = accentColor,
                    focusedPlaceholderColor = subtitleColor,
                    unfocusedPlaceholderColor = subtitleColor
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = accentColor) },
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = titleColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBackground,
                    unfocusedContainerColor = fieldBackground,
                    disabledContainerColor = fieldBackground,
                    errorContainerColor = fieldBackground,
                    focusedTextColor = titleColor,
                    unfocusedTextColor = titleColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = fieldBorder,
                    focusedLabelColor = subtitleColor,
                    unfocusedLabelColor = subtitleColor,
                    cursorColor = accentColor,
                    focusedPlaceholderColor = subtitleColor,
                    unfocusedPlaceholderColor = subtitleColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = accentColor) },
                    shape = RoundedCornerShape(14.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = titleColor),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = fieldBackground,
                        unfocusedContainerColor = fieldBackground,
                        disabledContainerColor = fieldBackground,
                        errorContainerColor = fieldBackground,
                        focusedTextColor = titleColor,
                        unfocusedTextColor = titleColor,
                        focusedIndicatorColor = accentColor,
                        unfocusedIndicatorColor = fieldBorder,
                        focusedLabelColor = subtitleColor,
                        unfocusedLabelColor = subtitleColor,
                        cursorColor = accentColor,
                        focusedPlaceholderColor = subtitleColor,
                        unfocusedPlaceholderColor = subtitleColor
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1.4f)) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = titleColor),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = fieldBackground,
                            unfocusedContainerColor = fieldBackground,
                            disabledContainerColor = fieldBackground,
                            errorContainerColor = fieldBackground,
                            focusedTextColor = titleColor,
                            unfocusedTextColor = titleColor,
                            focusedIndicatorColor = accentColor,
                            unfocusedIndicatorColor = fieldBorder,
                            focusedLabelColor = subtitleColor,
                            unfocusedLabelColor = subtitleColor,
                            cursorColor = accentColor,
                            focusedPlaceholderColor = subtitleColor,
                            unfocusedPlaceholderColor = subtitleColor
                        ),
                        trailingIcon = {
                            IconButton(onClick = { genderExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = accentColor)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genders.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = accentColor) },
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = titleColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBackground,
                    unfocusedContainerColor = fieldBackground,
                    disabledContainerColor = fieldBackground,
                    errorContainerColor = fieldBackground,
                    focusedTextColor = titleColor,
                    unfocusedTextColor = titleColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = fieldBorder,
                    focusedLabelColor = subtitleColor,
                    unfocusedLabelColor = subtitleColor,
                    cursorColor = accentColor,
                    focusedPlaceholderColor = subtitleColor,
                    unfocusedPlaceholderColor = subtitleColor
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = subtitleColor
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = accentColor) },
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = titleColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBackground,
                    unfocusedContainerColor = fieldBackground,
                    disabledContainerColor = fieldBackground,
                    errorContainerColor = fieldBackground,
                    focusedTextColor = titleColor,
                    unfocusedTextColor = titleColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = fieldBorder,
                    focusedLabelColor = subtitleColor,
                    unfocusedLabelColor = subtitleColor,
                    cursorColor = accentColor,
                    focusedPlaceholderColor = subtitleColor,
                    unfocusedPlaceholderColor = subtitleColor
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && age.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        if (password == confirmPassword) {
                            isLoading = true
                            errorMessage = null
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = task.result?.user
                                        if (user != null) {
                                            val profile = UserProfile(
                                                uid = user.uid,
                                                name = name,
                                                email = email,
                                                gender = gender,
                                                age = age.toIntOrNull() ?: 0,
                                                country = country
                                            )
                                            viewModel.saveUserProfile(profile) { success ->
                                                isLoading = false
                                                if (success) {
                                                    onSignUpSuccess()
                                                } else {
                                                    errorMessage = "Conta criada, mas erro ao salvar perfil."
                                                }
                                            }
                                        }
                                    } else {
                                        isLoading = false
                                        errorMessage = task.exception?.message ?: "Erro ao criar conta"
                                    }
                                }
                        } else {
                            errorMessage = "As senhas não coincidem"
                        }
                    } else {
                        errorMessage = "Preencha todos os campos"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White,
                    disabledContainerColor = accentColor.copy(alpha = 0.7f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Create account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account?",
                    color = subtitleColor,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onBackToLogin, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                    Text(
                        text = "Login",
                        color = accentDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
