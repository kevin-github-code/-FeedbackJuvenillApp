package com.kevin.feedbackjuvenill

import android.annotation.SuppressLint
import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import androidx.compose.ui.viewinterop.AndroidView
import com.kevin.feedbackjuvenill.ui.theme.FeedbackJuvenillAppTheme

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Surface
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow

import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeedbackJuvenillAppTheme {
                val viewModel: MainViewModel = viewModel()
                val user = viewModel.currentUser
                
                var showSignUp by remember { mutableStateOf(false) }
                
                Log.d("MainActivity", "Estado de Autenticação (Property): ${user?.email}")
                
                if (user != null) {
                    MainScreen(viewModel)
                } else {
                    if (showSignUp) {
                        SignUpScreen(
                            viewModel = viewModel,
                            onSignUpSuccess = {
                                viewModel.updateCurrentUser()
                                showSignUp = false
                            },
                            onBackToLogin = { showSignUp = false }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                viewModel.updateCurrentUser()
                            },
                            onNavigateToSignUp = { showSignUp = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val selectedItem = viewModel.selectedItem
    val showReportDialog = viewModel.showReportDialog
    val selectedNewsItem = viewModel.selectedNewsItem
    
    val items = listOf("Início", "TV Digital", "Notícias", "Sobre Nós")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.PlayArrow, Icons.Filled.Info, Icons.Filled.Person)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = androidx.compose.ui.graphics.Color(0xFFF4F8FD),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { 
                            viewModel.onItemSelected(index)
                            viewModel.onNewsItemClicked(null) // Reset news detail when changing tabs
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color(0xFF718096),
                            unselectedTextColor = Color(0xFF718096)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedItem == 2 && selectedNewsItem == null) { // Only show FAB on News list
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
                .background(Color(0xFFF4F8FD))
        ) {
            when (selectedItem) {
                0 -> HomeScreen(viewModel)
                1 -> TvScreen()
                2 -> {
                    if (selectedNewsItem == null) {
                        NewsScreen(viewModel)
                    } else {
                        DetailScreen(news = selectedNewsItem, onBack = { viewModel.onNewsItemClicked(null) })
                    }
                }
                3 -> AboutScreen()
                4 -> AdminScreen(viewModel)
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

    // pull-to-refresh state
    val isRefreshing = remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(isRefreshing.value, onRefresh = {
        // inicia refresh no ViewModel e mostra indicador
        isRefreshing.value = true
        viewModel.refreshNews(selectedTab)
        // simula fim do refresh depois que operações assíncronas terminarem
        // para world news fetchWorldNews atualiza state quando terminar
        // aqui fazemos um pequeno delay usando LaunchedEffect
    })

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Notícias",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 4.dp)
        )
        Text(
            text = "Informação que aproxima e inspira.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7A90),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
        )
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { viewModel.onNewsTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)) {

            // O conteúdo principal (lista)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val newsList = if (selectedTab == 0) viewModel.feedbackNews else viewModel.worldNewsList
                if (newsList.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Ainda não há notícias",
                            message = "Volte a consultar dentro de momentos para ver novas publicações."
                        )
                    }
                } else {
                    items(newsList) { item ->
                        NewsCard(item, onClick = { viewModel.onNewsItemClicked(item) })
                    }
                }
            }

            // Indicador de pull-to-refresh
            PullRefreshIndicator(
                refreshing = isRefreshing.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                scale = true,
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }

        // Observa quando worldNewsList mudar para desligar o indicador
        val worldNews by remember { derivedStateOf { viewModel.worldNewsList } }
        LaunchedEffect(worldNews) {
            // quando a lista mundo for atualizada, termina o indicador
            isRefreshing.value = false
        }

        // Para feedbackNews também desligar indicador quando alterar
        val feedbackNews by remember { derivedStateOf { viewModel.feedbackNews } }
        LaunchedEffect(feedbackNews) {
            isRefreshing.value = false
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DetailScreen(news: NewsItem, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F8FD))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Voltar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Web News Detail: se existir URL em description, extrai e abre em WebView interno
        val urlRegex = "(https?://[^\\s]+)".toRegex()
        val urlMatch = urlRegex.find(news.description)
        if (urlMatch != null) {
            val url = urlMatch.value
            AndroidView(factory = {
                WebView(it).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                }
            }, modifier = Modifier.fillMaxSize())
        } else {
            // Local News Detail
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = news.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = news.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = news.date, style = MaterialTheme.typography.labelSmall, color = Color(0xFF718096))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = news.description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .border(1.dp, Color(0xFFE4ECF7), MaterialTheme.shapes.extraLarge)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = news.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF10233F),
                lineHeight = 26.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5D6B7E),
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = news.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TvScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedVideoId by remember { mutableStateOf<String?>(null) }
    val tabs = listOf("Vídeos", "Shorts")
    val channelUrl = if (selectedTab == 0) {
        "https://www.youtube.com/@FeedbackJuvenilmz/videos"
    } else {
        "https://www.youtube.com/@FeedbackJuvenilmz/shorts"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FD))
    ) {
        if (selectedVideoId == null) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = "TV Digital",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Assista aos conteúdos mais recentes da Feedback Juvenil.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5D6B7E),
                    modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                imageVector = if (index == 0) Icons.Filled.PlayArrow else Icons.Filled.Info,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            key(channelUrl) {
                YoutubeChannelWebView(
                    url = channelUrl,
                    onVideoSelected = { selectedVideoId = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                TextButton(
                    onClick = { selectedVideoId = null }
                ) {
                    Text("Voltar para ${tabs[selectedTab]}")
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    AndroidView(factory = {
                        WebView(it).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mediaPlaybackRequiresUserGesture = false
                            loadUrl("https://www.youtube.com/embed/$selectedVideoId?autoplay=1&playsinline=1")
                        }
                    })
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YoutubeChannelWebView(
    url: String,
    onVideoSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val videoId = request?.url?.let(::youtubeVideoId)
                        return if (videoId != null) {
                            onVideoSelected(videoId)
                            true
                        } else {
                            false
                        }
                    }
                }
                loadUrl(url)
            }
        }
    )
}

private fun youtubeVideoId(uri: Uri): String? {
    val host = uri.host.orEmpty()
    if (!host.contains("youtube.com") && !host.contains("youtu.be")) return null
    val id = when {
        host.contains("youtu.be") -> uri.pathSegments.firstOrNull()
        uri.path == "/watch" -> uri.getQueryParameter("v")
        uri.pathSegments.firstOrNull() == "shorts" -> uri.pathSegments.getOrNull(1)
        else -> null
    }
    return id?.takeIf { it.matches(Regex("[A-Za-z0-9_-]{6,}")) }
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF4F8FD))
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Olá, ${viewModel.userProfile?.name?.split(" ")?.firstOrNull() ?: "Bem-vindo"}!",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF6B7A90)
                )
                Text(
                    text = "Feedback Juvenil",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.logout() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Gray
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_feedback_juvenil),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(52.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    if (viewModel.userProfile?.role == "admin") {
                                        viewModel.onItemSelected(4) // Admin Tab
                                    }
                                }
                            )
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Card - TV Digital
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clickable { viewModel.onItemSelected(1) },
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "TV DIGITAL AO VIVO",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Assista nossas transmissões agora",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions Row
        Text(
            text = "Ações Rápidas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton("Voz à Juventude", Icons.Filled.Create) { viewModel.onShowReportDialog(true) }
            QuickActionButton("Notícias", Icons.Filled.Info) { viewModel.onItemSelected(2) }
            QuickActionButton("Redes", Icons.Filled.Share) { viewModel.onItemSelected(1) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Latest News Preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Últimas Notícias",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { viewModel.onItemSelected(2) }) {
                Text("Ver tudo")
            }
        }

        val latestNews = viewModel.feedbackNews.firstOrNull()
        if (latestNews != null) {
            NewsCard(latestNews, onClick = { viewModel.onNewsItemClicked(latestNews) })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AboutScreen() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF4F8FD))
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.icone_semtexto),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Bem Vindo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Feedback Juvenil",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Contactos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(text = "Numeros: 875443806", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Numeros: 835654536", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Email: fjuvenilmz@gmail.com", style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Localização: Moçambique, Maputo, Distrito de Marracuene",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Biografia",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "A Feedback Juvenil é uma multi-plataforma de midia digital inovadora, criada para dar voz a juventude, valorizar a cultura e impulsionar o desenvolvimento comunitario.\n\n**A Nossa Missão**\nConectar, informar e engajar as novas gerações através de conteúdos dinâmicos que abordam desde a atualidade, desporto e cultura até debates aprofundados sobre os temas que moldam o nosso futuro. Acreditamos que a informação livre, criativa e plural é a chave para a transformação social.\n\n**O Nosso Lema**\n\"Juntos, o futuro é de ouro.\"",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    val users = viewModel.allUsers
    
    LaunchedEffect(Unit) {
        viewModel.fetchAllUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FD))
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Painel Admin",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { viewModel.onItemSelected(0) }) {
                Icon(Icons.Filled.Home, contentDescription = "Sair")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estatísticas Rápidas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Total de Usuários: ${users.size}", style = MaterialTheme.typography.titleMedium)
                val avgAge = if (users.isNotEmpty()) users.map { it.age }.average() else 0.0
                Text(text = "Média de Idade: ${"%.1f".format(avgAge)} anos", style = MaterialTheme.typography.bodyMedium)
                
                val maleCount = users.count { it.gender == "Masculino" }
                val femaleCount = users.count { it.gender == "Feminino" }
                Text(text = "Gênero: $maleCount M / $femaleCount F", style = MaterialTheme.typography.bodyMedium)
                
                val countries = users.groupBy { it.country }.mapValues { it.value.size }
                Text(text = "Países: ${countries.entries.joinToString { "${it.key}: ${it.value}" }}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Lista de Usuários", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(users) { user ->
                ListItem(
                    headlineContent = { Text(user.name) },
                    supportingContent = { Text("${user.email} | ${user.age} anos | ${user.gender}") },
                    overlineContent = { Text("Role: ${user.role}") },
                    trailingContent = {
                        if (user.role == "admin") {
                            Icon(Icons.Filled.Settings, contentDescription = null, tint = Color(0xFFFFD700))
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.size(64.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7A90),
                textAlign = TextAlign.Center
            )
        }
    }
}
