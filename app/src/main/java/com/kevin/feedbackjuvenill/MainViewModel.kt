package com.kevin.feedbackjuvenill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {
    
    private val auth by lazy { FirebaseAuth.getInstance() }
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUserState: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        viewModelScope.launch(Dispatchers.Main) {
            currentUser = user
            _currentUser.value = user
            Log.d("MainViewModel", "AuthStateListener: Usuário atualizado para ${user?.email}")
        }
    }

    init {
        try {
            val initialUser = FirebaseAuth.getInstance().currentUser
            currentUser = initialUser
            _currentUser.value = initialUser
            FirebaseAuth.getInstance().addAuthStateListener(authListener)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Erro ao inicializar Firebase no ViewModel", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Erro ao remover listener", e)
        }
    }

    private val newsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    // State for navigation
    var selectedItem by mutableIntStateOf(0)
        private set

    // State for the report dialog
    var showReportDialog by mutableStateOf(false)
        private set

    // State for news tabs
    var selectedNewsTab by mutableIntStateOf(0)
        private set

    // State for selected news detail
    var selectedNewsItem by mutableStateOf<NewsItem?>(null)
        private set

    // Real world news from API
    var worldNewsList by mutableStateOf<List<NewsItem>>(emptyList())
        private set

    // News Data (Placeholder for Feedback Juvenil)
    val feedbackNews = listOf(
        NewsItem("Novo Centro Juvenil em Marracuene", "Um novo espaço para cultura e lazer será inaugurado...", "21 Ago 2026", "Local"),
        NewsItem("Entrevista com Artistas Locais", "Conheça a história dos talentos que estão a brilhar em Maputo.", "20 Ago 2026", "Cultura"),
        NewsItem("Debate sobre Educação", "Jovens reúnem-se para discutir o futuro do ensino técnico.", "19 Ago 2026", "Sociedade")
    )

    // Actions
    fun onItemSelected(index: Int) {
        selectedItem = index
    }

    fun onShowReportDialog(show: Boolean) {
        showReportDialog = show
    }

    fun onNewsTabSelected(index: Int) {
        selectedNewsTab = index
        if (index == 1 && worldNewsList.isEmpty()) {
            fetchWorldNews()
        }
    }

    fun onNewsItemClicked(item: NewsItem?) {
        selectedNewsItem = item
    }

    fun logout() {
        Log.d("MainViewModel", "Iniciando logout...")
        auth.signOut()
        // O listener deve cuidar do resto, mas vamos garantir
        viewModelScope.launch(Dispatchers.Main) {
            currentUser = null
            _currentUser.value = null
        }
    }

    fun updateCurrentUser() {
        val user = auth.currentUser
        Log.d("MainViewModel", "updateCurrentUser chamado. Usuário: ${user?.email}")
        viewModelScope.launch(Dispatchers.Main) {
            currentUser = user
            _currentUser.value = user
        }
    }

    private fun fetchWorldNews() {
        viewModelScope.launch {
            try {
                val response = newsApiService.getTopHeadlines()
                worldNewsList = response.articles.map { article ->
                    NewsItem(
                        title = article.title,
                        description = article.description ?: "",
                        date = article.publishedAt,
                        category = article.source.name
                    )
                }
            } catch (e: Exception) {
                // Em caso de erro, poderíamos mostrar uma mensagem ao usuário
                e.printStackTrace()
            }
        }
    }
}
