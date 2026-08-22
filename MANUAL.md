# 📘 Manual Técnico: App Feedback Juvenil

Este manual foi criado para ajudar você a entender a estrutura, a linguagem e as escolhas técnicas feitas no desenvolvimento do seu aplicativo até agora.

---

## 1. Linguagem e Framework Principal
*   **Linguagem: Kotlin.** É a linguagem oficial para Android. Ela é "concisa" (escreve-se menos para fazer mais) e segura.
*   **Interface: Jetpack Compose.** Em vez de usar arquivos XML antigos, usamos código Kotlin para "declarar" como a tela deve ser. Se você quer um texto, você chama a função `Text()`.

---

## 2. Arquitetura: MVVM (Model-View-ViewModel)
Para o app não virar uma bagunça, separamos as responsabilidades:

1.  **Model (NewsItem.kt):** Define apenas **o que é** o dado (ex: uma notícia tem título e data).
2.  **ViewModel (MainViewModel.kt):** É o **cérebro**. Ele decide *quando* buscar notícias e *guarda* qual aba está aberta. Ele não sabe "desenhar" na tela, ele apenas fornece os dados.
3.  **View (MainActivity.kt):** É a **cara** do app. Ela observa o ViewModel e desenha os botões e textos.

---

## 3. Entendendo o Código (Componentes)

### A. Como as imagens são tratadas?
As imagens ficam na pasta `app/src/main/res/drawable`.
No código, usamos a função:
```kotlin
Image(
    painter = painterResource(id = R.drawable.logo_feedback_juvenil),
    contentDescription = "Descrição para cegos/acessibilidade"
)
```

### B. Como o texto é tratado?
Usamos a função `Text()`. A estilização (tamanho, negrito) vem do arquivo `ui/theme/Type.kt`.
```kotlin
Text(
    text = "Feedback Juvenil",
    style = MaterialTheme.typography.headlineMedium // Usa o estilo pré-definido
)
```

### C. Como funcionam as listas (News)?
Usamos o **LazyColumn**. Ele é "preguiçoso" (lazy) porque só desenha na tela as notícias que você está vendo no momento, economizando bateria e memória.

### D. O que é "State" (Estado)?
Você verá muito `mutableStateOf` ou `remember`. No Compose, a tela não muda sozinha. Se você clica num botão, você muda um "Estado", e o Compose redesenha a tela automaticamente com o novo valor.

---

## 4. Integrações Externas (Internet)
*   **Retrofit:** É o carteiro do app. Ele vai até a internet (NewsAPI), pega os dados e traz de volta.
*   **WebView:** É como se tivéssemos um "mini navegador Chrome" dentro do app para mostrar o YouTube ou os detalhes das notícias do mundo.

---

## 5. Dúvida sobre o YouTube
**O YouTube é menos burocrático?** Sim!
Para apenas *exibir* vídeos (como fizemos com a WebView), não há burocracia. Se quisermos listar os vídeos de forma automática e elegante, usamos a **YouTube Data API v3**, que exige apenas uma chave simples do Google Cloud, sem a necessidade de SMS ou aprovações complexas como no Facebook.

---

## 6. Resumo das Pastas
*   `MainActivity.kt`: Onde a interface acontece.
*   `MainViewModel.kt`: Onde a lógica de dados mora.
*   `res/drawable`: Onde seu logo e ícones moram.
*   `res/values/themes.xml`: Onde configuramos a Splash Screen (abertura).

> [!TIP]
> Sempre que ler o código, procure por funções que começam com `@Composable`. Elas são os blocos de construção da sua interface.

---
**Moçambique rumo ao futuro digital! 🇲🇿**
