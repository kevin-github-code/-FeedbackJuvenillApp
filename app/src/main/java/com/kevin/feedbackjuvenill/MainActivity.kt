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
import androidx.compose.runtime.mutableIntStateOf
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
import com.kevin.feedbackjuvenill.ui.theme.FeedbackJuvenillAppTheme

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
fun MainScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
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
                        onClick = { selectedItem = index }
                    )
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
                2 -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Feed de Notícias", style = MaterialTheme.typography.headlineMedium)
                    Text("(Em breve)", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun TvScreen() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "TV Digital & Lives",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Text(
            text = "Acompanhe nossas transmissões ao vivo nas redes sociais oficiais:",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        SocialButton(
            text = "Facebook Live",
            color = Color(0xFF1877F2),
            icon = Icons.Filled.Share,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/feedbackjuvenil"))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SocialButton(
            text = "YouTube Channel",
            color = Color(0xFFFF0000),
            icon = Icons.Filled.PlayArrow,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@feedbackjuvenil"))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SocialButton(
            text = "Instagram",
            color = Color(0xFFE4405F),
            icon = Icons.Filled.Info,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/feedbackjuvenil"))
                context.startActivity(intent)
            }
        )
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
