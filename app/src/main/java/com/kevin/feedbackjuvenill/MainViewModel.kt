package com.kevin.feedbackjuvenill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    
    // State for navigation
    var selectedItem by mutableIntStateOf(0)
        private set

    // State for the report dialog
    var showReportDialog by mutableStateOf(false)
        private set

    // State for news tabs
    var selectedNewsTab by mutableIntStateOf(0)
        private set

    // News Data
    val feedbackNews = listOf(
        NewsItem("Novo Centro Juvenil em Marracuene", "Um novo espaço para cultura e lazer será inaugurado...", "21 Ago 2026", "Local"),
        NewsItem("Entrevista com Artistas Locais", "Conheça a história dos talentos que estão a brilhar em Maputo.", "20 Ago 2026", "Cultura"),
        NewsItem("Debate sobre Educação", "Jovens reúnem-se para discutir o futuro do ensino técnico.", "19 Ago 2026", "Sociedade")
    )

    val worldNews = listOf(
        NewsItem("Inovações Tecnológicas 2026", "As novas tendências que estão a mudar o mercado de trabalho global.", "21 Ago 2026", "Tech"),
        NewsItem("Cimeira do Clima", "Líderes mundiais discutem metas para a sustentabilidade.", "20 Ago 2026", "Ambiente")
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
    }
}
