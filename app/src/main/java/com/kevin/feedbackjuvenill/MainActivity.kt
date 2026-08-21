package com.kevin.feedbackjuvenill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import com.kevin.feedbackjuvenill.ui.theme.FeedbackJuvenillAppTheme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Dialog

import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeedbackJuvenillAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val selectedItem = viewModel.selectedItem
    val showReportDialog = viewModel.showReportDialog
    
    val items = listOf("Início", "TV Digital", "Notícias")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.PlayArrow, Icons.Filled.Info)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { viewModel.onItemSelected(index) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedItem == 2) { // Only show FAB on News tab
                FloatingActionButton(
                    onClick = { viewModel.onShowReportDialog(true) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Create, contentDescription = "Reportar", tint = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> TvScreen()
                2 -> NewsScreen(viewModel)
            }
            
            if (showReportDialog) {
                ReportDialog(onDismiss = { viewModel.onShowReportDialog(false) })
            }
        }
    }
}

@Composable
fun NewsScreen(viewModel: MainViewModel) {
    val selectedTab = viewModel.selectedNewsTab
    val tabs = listOf("Feedback Juvenil", "Mundo")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { viewModel.onNewsTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val newsList = if (selectedTab == 0) viewModel.feedbackNews else viewModel.worldNewsList
            items(newsList) { item ->
                NewsCard(item)
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = news.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = news.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Text(
                text = news.date,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ReportDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var requestCoverage by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Voz à Juventude",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Publique uma notícia ou denúncia",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Acontecimento") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição detalhada") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = requestCoverage,
                        onCheckedChange = { requestCoverage = it }
                    )
                    Text(
                        text = "Pedir presença da TV (Cobertura)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            // Aqui futuramente enviaremos para um servidor
                            onDismiss()
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}

@Composable
fun TvScreen() {
    var showLivePlayer by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TV Digital & Lives",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        if (!showLivePlayer) {
            Text(
                text = "Escolha uma plataforma para assistir ao vivo:",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            SocialButton(
                text = "Assistir no YouTube",
                color = Color(0xFFFF0000),
                icon = Icons.Filled.PlayArrow,
                onClick = { showLivePlayer = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SocialButton(
                text = "Facebook Live (Externo)",
                color = Color(0xFF1877F2),
                icon = Icons.Filled.Share,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/feedbackjuvenil/live"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SocialButton(
                text = "Instagram (Externo)",
                color = Color(0xFFE4405F),
                icon = Icons.Filled.Info,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/feedbackjuvenil"))
                    context.startActivity(intent)
                }
            )
        } else {
            // Player Integrado (YouTube Mobile View)
            Column(modifier = Modifier.fillMaxSize()) {
                TextButton(
                    onClick = { showLivePlayer = false },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text("← Voltar")
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    AndroidView(factory = {
                        WebView(it).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            loadUrl("https://www.youtube.com/@feedbackjuvenil/live")
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun SocialButton(
    text: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_feedback_juvenil),
            contentDescription = "Logo Feedback Juvenil",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Feedback Juvenil",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Juntos, o futuro é de ouro",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Informação e voz para a juventude de Marracuene e Maputo.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FeedbackJuvenillAppTheme {
        MainScreen()
    }
}
