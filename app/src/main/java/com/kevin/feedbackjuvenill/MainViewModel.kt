package com.kevin.feedbackjuvenill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val gender: String = "",
    val age: Int = 0,
    val country: String = "Moçambique",
    val role: String = "user" // Para futura tela admin
)

class MainViewModel : ViewModel() {
    
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUserState: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        viewModelScope.launch(Dispatchers.Main) {
            currentUser = user
            _currentUser.value = user
            if (user != null) {
                fetchUserProfile(user.uid)
            } else {
                userProfile = null
            }
            Log.d("MainViewModel", "AuthStateListener: Usuário atualizado para ${user?.email}")
        }
    }

    init {
        try {
            val initialUser = FirebaseAuth.getInstance().currentUser
            currentUser = initialUser
            _currentUser.value = initialUser
            if (initialUser != null) {
                fetchUserProfile(initialUser.uid)
            }
            FirebaseAuth.getInstance().addAuthStateListener(authListener)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Erro ao inicializar Firebase no ViewModel", e)
        }
    }

    private fun fetchUserProfile(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val document = db.collection("users").document(uid).get().await()
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    viewModelScope.launch(Dispatchers.Main) {
                        userProfile = profile
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Erro ao buscar perfil do usuário", e)
            }
        }
    }

    fun saveUserProfile(profile: UserProfile, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(profile.uid).set(profile).await()
                viewModelScope.launch(Dispatchers.Main) {
                    userProfile = profile
                    onComplete(true)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Erro ao salvar perfil", e)
                viewModelScope.launch(Dispatchers.Main) {
                    onComplete(false)
                }
            }
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

    // News Data (carregada a partir dos vídeos publicados no canal YouTube — Feedback Juvenil)
    var feedbackNews by mutableStateOf(listOf(
        NewsItem("CDJ DE MARRACUENE UNE JUVENTUDE: Ndixe vence Matalane nos penáltis numa tarde de festa", "Vídeo publicado no YouTube — 4 visualizações. Link: https://www.youtube.com/watch?v=GaNAqwPXs6c", "2026-08-25", "YouTube"),
        NewsItem("𝐌𝐀𝐑𝐑𝐀𝐂𝐔𝐄𝐍𝐄 𝐍𝐎 𝐂𝐄𝐍𝐓𝐑𝐎 𝐃𝐎 𝐏𝐀Í𝐒: 𝐋é𝐠𝐮𝐚 𝐫𝐮𝐦𝐨  à 𝐅𝐀𝐂𝐈𝐌 𝐧𝐮𝐦 𝐯𝐢𝐛𝐫𝐚𝐧𝐭𝐞 𝐦𝐚𝐧𝐢𝐟𝐞𝐬𝐭𝐨 𝐝𝐞 𝐮𝐧𝐢ã𝐨 𝐞 𝐧𝐞𝐠ó𝐜𝐢𝐨𝐬!", "Vídeo publicado no YouTube — 3 visualizações. Link: https://www.youtube.com/watch?v=kSwD4CbUNv4", "2026-08-24", "YouTube"),
        NewsItem("𝐎 𝐏𝐎𝐍𝐓𝐎 𝐉𝐎𝐕𝐄𝐌 𝐃𝐄𝐒𝐓𝐄 𝐒Á𝐁𝐀𝐃𝐎 𝐄𝐒𝐓Á 𝐈𝐌𝐏𝐄𝐑𝐃Í𝐕𝐄𝐋 ,𝐏𝐎𝐈𝐒 𝐑𝐄𝐂𝐄𝐁𝐄𝐌𝐎𝐒 𝐎 𝐏𝐑𝐄𝐒𝐈𝐃𝐄𝐍𝐓𝐄 𝐃𝐎 𝐂𝐃𝐉, 𝐑𝐀𝐅𝐀𝐄𝐋 𝐌𝐀𝐓𝐒𝐔𝐕𝐄", "Vídeo publicado no YouTube — 43 visualizações. Link: https://www.youtube.com/watch?v=Roe7Zso_iUc", "2026-05-01", "YouTube"),
        NewsItem("PONTO JOVEM:ENTREVISTA COM O ACADÉMICO RÉGIO CONRADO |18.04.2026", "Vídeo publicado no YouTube — 5177 visualizações. Link: https://www.youtube.com/watch?v=brDnago_2wQ", "2026-04-20", "YouTube"),
        NewsItem("PONTO JOVEM:ENTREVISTA COM O ALFAIATE DE PALAVRAS | 04.04.2026", "Vídeo publicado no YouTube — 44 visualizações. Link: https://www.youtube.com/watch?v=7wvfmCwvUWk", "2026-04-20", "YouTube"),
        NewsItem("𝐑É𝐆𝐈𝐎 𝐂𝐎𝐍𝐑𝐀𝐃𝐎 𝐃𝐈𝐙 𝐐𝐔𝐄 𝐎𝐒 𝐉𝐎𝐕𝐄𝐍𝐒 𝐏𝐀𝐒𝐒𝐀𝐌 𝐌𝐀𝐈𝐒 𝐓𝐄𝐌𝐏𝐎 𝐄𝐌𝐁𝐑𝐈𝐀𝐆𝐀𝐃𝐎𝐒!", "Vídeo publicado no YouTube — 5306 visualizações. Link: https://www.youtube.com/watch?v=WwNFge2teBM", "2026-04-19", "YouTube"),
        NewsItem("CHEIAS NA CIDADE DE MAPUTO AVENIDA 25 DE SETEMBRO #moçambique #noticias #cheias", "Vídeo publicado no YouTube — 1412 visualizações. Link: https://www.youtube.com/watch?v=DYr1OSBLCjM", "2026-03-13", "YouTube"),
        NewsItem("𝐎 𝐏𝐎𝐍𝐓𝐎 𝐉𝐎𝐕𝐄𝐌 𝐐𝐔𝐀𝐒𝐄 𝐅𝐈𝐂𝐎𝐔 𝐒𝐄𝐌 𝐀𝐏𝐑𝐄𝐒𝐄𝐍𝐓𝐀𝐃𝐎𝐑,𝐀𝐂𝐎𝐌𝐏𝐀𝐍𝐇𝐄 𝐎 𝐌𝐎𝐌𝐄𝐍𝐓𝐎 𝐄𝐌 𝐐𝐔𝐄 𝐀 𝐀𝐋𝐌𝐀 𝐃𝐎 𝐀𝐏𝐑𝐄𝐒𝐄𝐍𝐓𝐀𝐃𝐎𝐑 𝐒𝐀𝐈𝐔...", "Vídeo publicado no YouTube — 240 visualizações. Link: https://www.youtube.com/watch?v=JKnDEhpF8K4", "2026-03-10", "YouTube"),
        NewsItem("Na Polana Caniço já estão a nadar em menos de uma hora.", "Vídeo publicado no YouTube — 782 visualizações. Link: https://www.youtube.com/watch?v=BswXqrB-Dak", "2026-03-07", "YouTube"),
        NewsItem("Um camião da empresa Super Steel capotou na manhã de hoje,no bairro Nhongonhane", "Vídeo publicado no YouTube — 16 visualizações. Link: https://www.youtube.com/watch?v=sCmAlxf-_YQ", "2026-02-19", "YouTube"),
        NewsItem("Mural ambiental é inaugurado para reforçar educação ecológica em Marracuene", "Vídeo publicado no YouTube — 8 visualizações. Link: https://www.youtube.com/watch?v=HnZd2BZhs6Q", "2026-02-16", "YouTube"),
        NewsItem("ELETRICIDADE DE MOÇAMBIQUE SEM MATERIAL?EXPLOSÃO EM POSTE DEIXA RESIDÊNCIAS DE MEMO SEM ENERGIA", "Vídeo publicado no YouTube — 51 visualizações. Link: https://www.youtube.com/watch?v=J4u-OUe_PbY", "2026-02-06", "YouTube"),
        NewsItem("131 Anos da Batalha de Gwaza Muthini | Cerimónia Tradicional em Marracuene (02/02/2025)", "Vídeo publicado no YouTube — 219 visualizações. Link: https://www.youtube.com/watch?v=AowK8lSIFZ0", "2026-02-02", "YouTube"),
        NewsItem("Centro de Acolhimento para Vítimas das Cheias em  Marracuene", "Vídeo publicado no YouTube — 160 visualizações. Link: https://www.youtube.com/watch?v=P5_FgMwzudk", "2026-01-31", "YouTube"),
        NewsItem("MARRACUENE MORADORES DE MACANETA PEDEM AJUDA!", "Vídeo publicado no YouTube — 1678 visualizações. Link: https://www.youtube.com/watch?v=WiPKlNsDEW4", "2026-01-28", "YouTube"),
        NewsItem("Roda de Conversa reúne jovens em Marracuene para refletir sobre identidade africana", "Vídeo publicado no YouTube — 79 visualizações. Link: https://www.youtube.com/watch?v=K92rElQw-tM", "2026-01-26", "YouTube"),
        NewsItem("CHEIAS NO BAIRRO FAFTINE FAMILIAS DORMEM ENCIMA DA CASA", "Vídeo publicado no YouTube — 896 visualizações. Link: https://www.youtube.com/watch?v=H_nSniocwow", "2026-01-26", "YouTube"),
        NewsItem("MARRACUENE,FAMÍLIAS DO BAIRRO FAFTINE ABADONAM AS SUAS CASAS POR ESTAREM TOTALMENTE INUNDADAS", "Vídeo publicado no YouTube — 2610 visualizações. Link: https://www.youtube.com/watch?v=2BCyeDWG804", "2026-01-24", "YouTube"),
        NewsItem("Emergência Zona Sul:EN1 interrompida chuvas isolam o Bairro 3 de Fevereiro e paralisam a circulação", "Vídeo publicado no YouTube — 10877 visualizações. Link: https://www.youtube.com/watch?v=xnoOfClSH-I", "2026-01-23", "YouTube"),
        NewsItem("CHEIAS EM 3 DE FEVEREIRO", "Vídeo publicado no YouTube — 7305 visualizações. Link: https://www.youtube.com/watch?v=FADuvPp07Lo", "2026-01-22", "YouTube"),
        NewsItem("ALERTA MÁXIMO DEVIDO AO AUMENTO DO CAUDAL DO RIO IMCOMÁTI", "Vídeo publicado no YouTube — 2122 visualizações. Link: https://www.youtube.com/watch?v=kKvMSxTwzqs", "2026-01-22", "YouTube"),
        NewsItem("MARRACUENE,ESTRADA DA MACANETA INTERROPIDA PELAS ÁGUAS.", "Vídeo publicado no YouTube — 15465 visualizações. Link: https://www.youtube.com/watch?v=nxJ7mbR448Y", "2026-01-21", "YouTube"),
        NewsItem("IMPACTO DAS CHUVAS TORRENCIAIS EM MUMEMO", "Vídeo publicado no YouTube — 1238 visualizações. Link: https://www.youtube.com/watch?v=pUs73YfxbRI", "2026-01-20", "YouTube"),
        NewsItem("Entretenimento Total |Estreia-EP-01", "Vídeo publicado no YouTube — 53 visualizações. Link: https://www.youtube.com/watch?v=I3g_upMdM2o", "2026-01-18", "YouTube"),
        NewsItem("HORA DA COMUNIDADE!IMPACTO DAS CHUVAS TORRÊNCIAIS EM ALGUNS BAIRROS DE MARRACUENE", "Vídeo publicado no YouTube — 1399 visualizações. Link: https://www.youtube.com/watch?v=WEbKILs0VBY", "2026-01-15", "YouTube"),
        NewsItem("IMPACTO DAS CHUVAS TORRENCIAIS EM MARRACUENE", "Vídeo publicado no YouTube — 1558 visualizações. Link: https://www.youtube.com/watch?v=1Pv4MKRCDSg", "2026-01-14", "YouTube"),
        NewsItem("PONTO JOVEM:ENTREVISTA COM BÉU INSRAEL | EPISÓDIO O7.2025", "Vídeo publicado no YouTube — 36 visualizações. Link: https://www.youtube.com/watch?v=lFvkqTvItNw", "2025-11-23", "YouTube"),
        NewsItem("BÉU ISRAEL DIZ QUE NÃO PODEMOS ESPERAR PELO GOVERNO..", "Vídeo publicado no YouTube — 1085 visualizações. Link: https://www.youtube.com/watch?v=VYUsFr1uDyg", "2025-11-19", "YouTube"),
        NewsItem("IMEDIATISMO? OU A VIDA É CURTA MESMO🤔NÃO PERCA ESTE SABADO O PROGRAMA PONTO JOVEM COM A BÉU ISRAEL", "Vídeo publicado no YouTube — 1320 visualizações. Link: https://www.youtube.com/watch?v=cgJbqRc4yR8", "2025-11-18", "YouTube"),
        NewsItem("PONTO JOVEM:ENTREVISTA COM O ACTIVISTA CARLOS SERRA |EPISÓDIO 06.2025", "Vídeo publicado no YouTube — 30 visualizações. Link: https://www.youtube.com/watch?v=5TaXFay0PIs", "2025-11-14", "YouTube"),
        NewsItem("O RAPPER MOÇAMBICANO DJIMETTA FEZ UM GRANDE SHOW NA FEIRA GASTRONOMICA DA ECOSAF", "Vídeo publicado no YouTube — 54 visualizações. Link: https://www.youtube.com/watch?v=IdQ3ytWOF80", "2025-11-13", "YouTube"),
        NewsItem("suposto mandante da agressão a renomada Paulina Chiziane 😮", "Vídeo publicado no YouTube — 1221 visualizações. Link: https://www.youtube.com/watch?v=bJrtOVw6cZY", "2025-11-04", "YouTube"),
        NewsItem("PAULINA CHIZIANE DIZ QUE INSULTARAM  A TIMBILA", "Vídeo publicado no YouTube — 3307 visualizações. Link: https://www.youtube.com/watch?v=nnUg7zGTIsE", "2025-11-02", "YouTube"),
        NewsItem("EDUCADOR É DEVER DO PROFESSOR OU DOS PAIS?🤔", "Vídeo publicado no YouTube — 877 visualizações. Link: https://www.youtube.com/watch?v=HokvNfBHDkI", "2025-11-02", "YouTube"),
        NewsItem("EDUCAÇÃO DE FAZ DE CONTA ONDE O PROFESSOR TEM PROBLEMAS O DIRECTOR TEM PROBLEMAS O ALUNO TEM .", "Vídeo publicado no YouTube — 1254 visualizações. Link: https://www.youtube.com/watch?v=Lu3DFea-_2I", "2025-10-31", "YouTube"),
        NewsItem("EDUCAÇÃO DE MOÇAMBIQUE NÃO SATISFAZ NÉM 1% DAS NOSSAS NECESSIDADES DIZ O FILÓSOFO JACINTO PEQUENINO!", "Vídeo publicado no YouTube — 1101 visualizações. Link: https://www.youtube.com/watch?v=I9kzgGPEQnU", "2025-10-29", "YouTube"),
        NewsItem("professor e filósofo jacinto,diz não ser apático ao sociativemos por conta do sistema em Moçambique", "Vídeo publicado no YouTube — 847 visualizações. Link: https://www.youtube.com/watch?v=cFkI6ZZfBD0", "2025-10-28", "YouTube"),
        NewsItem("professor Jacinto,Questiona qual é o projecto para Moçambique", "Vídeo publicado no YouTube — 797 visualizações. Link: https://www.youtube.com/watch?v=VCYDNJ9Daaw", "2025-10-26", "YouTube"),
        NewsItem("Africa é mãe?e quem é o pai?presidentes gordos por comer dinheiro do povo", "Vídeo publicado no YouTube — 930 visualizações. Link: https://www.youtube.com/watch?v=UPvNJ7fmfak", "2025-10-26", "YouTube"),
        NewsItem("Dizem que os Jovens não estão preparados,aquestão é quem é o culpado quem não preparou,diz o profess", "Vídeo publicado no YouTube — 2330 visualizações. Link: https://www.youtube.com/watch?v=WjKvadtJSRM", "2025-10-25", "YouTube"),
        NewsItem("PONTO JOVEM:ENTREVISTA COM O PROFESSOR JACINTO PEQUENINO |EPISÓDIO 05.2025", "Vídeo publicado no YouTube — 55 visualizações. Link: https://www.youtube.com/watch?v=kmEPY0iJi34", "2025-10-25", "YouTube"),
        NewsItem("Professor Jacinto Pequenino #juventudemoçambicana #noticias #caminhoparaumavidamelhor #pontojovem", "Vídeo publicado no YouTube — 281 visualizações. Link: https://www.youtube.com/watch?v=b9sGdEubABw", "2025-10-23", "YouTube"),
        NewsItem("O RESGATE (Comissão Envagelistica,22 de Outubro 2025)", "Vídeo publicado no YouTube — 23 visualizações. Link: https://www.youtube.com/watch?v=eIY9svPcQHA", "2025-10-21", "YouTube"),
        NewsItem("ALUSIVO AO MÊS DOS PROFESSORES #juventudemoçambicana #caminhoparaumavidamelhor #pontojovem #africa", "Vídeo publicado no YouTube — 2158 visualizações. Link: https://www.youtube.com/watch?v=tum2xl0hCf8", "2025-10-20", "YouTube"),
        NewsItem("PONTO JOVEM:ENTREVISTA COM O DESPORTISTA E PROFESSOR JOSÉ NETO EPISÓDIO 04.2025", "Vídeo publicado no YouTube — 42 visualizações. Link: https://www.youtube.com/watch?v=Bya1Pn4cDDs", "2025-10-19", "YouTube"),
        NewsItem("A RENOMADA ESCRITORA PAULINA CHIZIANE FALA APÓS AUDIÊNCIA PRELIMINAR", "Vídeo publicado no YouTube — 8821 visualizações. Link: https://www.youtube.com/watch?v=YUTMIAEhvac", "2025-10-14", "YouTube"),
        NewsItem("HINO DA ECOSAF-MARRACUENE", "Vídeo publicado no YouTube — 144 visualizações. Link: https://www.youtube.com/watch?v=DLUHCltMzyg", "2025-10-13", "YouTube"),
        NewsItem("Professor Jacinto Pequenino no ponto jovém", "Vídeo publicado no YouTube — 1051 visualizações. Link: https://www.youtube.com/watch?v=mEbDph_-osE", "2025-10-13", "YouTube"),
        NewsItem("professor Palinga nos 15 anos da Sagrada familia", "Vídeo publicado no YouTube — 1398 visualizações. Link: https://www.youtube.com/watch?v=6JDWSTZrQ4E", "2025-10-13", "YouTube"),
        NewsItem("Mesmo com chuva, o brilho não apagou!", "Vídeo publicado no YouTube — 20 visualizações. Link: https://www.youtube.com/watch?v=T012f3yVpao", "2025-09-30", "YouTube"),
        NewsItem("PONTO JOVEM - EPISÓDIO 01.2025", "Vídeo publicado no YouTube — 52 visualizações. Link: https://www.youtube.com/watch?v=h_o-g4fiJWY", "2025-07-26", "YouTube"),
        NewsItem("Shafee Sidat diz que todos artistas locais que actuam nos festivais de marracuene são pagos!", "Vídeo publicado no YouTube — 20 visualizações. Link: https://www.youtube.com/watch?v=eao4NHsWTMc", "2025-05-21", "YouTube"),
        NewsItem("shafee Sidat fala sobre os artistas locais🤯 #1demaio #diadotrabalhador #shafeeSidat", "Vídeo publicado no YouTube — 194 visualizações. Link: https://www.youtube.com/watch?v=js9LJG1KRJo", "2025-05-21", "YouTube"),
        NewsItem("Marracuene Celebra o Dia do Trabalhador com Desfiles, Discursos e Atividades Culturais", "Vídeo publicado no YouTube — 18 visualizações. Link: https://www.youtube.com/watch?v=RdXd-soGFeg", "2025-05-18", "YouTube"),
        NewsItem("Entrevista Exclusiva com os Gladiadores do Berro ao Extremo(2025)", "Vídeo publicado no YouTube — 53 visualizações. Link: https://www.youtube.com/watch?v=TWNdSAX_XT4", "2025-05-05", "YouTube")
    ))

    // Função para refresh acionada pelo pull-to-refresh
    fun refreshNews(selectedTabIndex: Int) {
        if (selectedTabIndex == 1) {
            fetchWorldNews()
        } else {
            // Para feedbackNews (estático no momento) apenas força recomposição
            feedbackNews = feedbackNews.toList()
        }
    }

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

    var allUsers by mutableStateOf<List<UserProfile>>(emptyList())
        private set

    fun fetchAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = db.collection("users").get().await()
                val users = snapshot.toObjects(UserProfile::class.java)
                viewModelScope.launch(Dispatchers.Main) {
                    allUsers = users
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Erro ao buscar todos os usuários", e)
            }
        }
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
