package com.kevin.feedbackjuvenill

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String = "ecf9df0d537b4f14834f608d94ace545"
    ): NewsResponse
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val publishedAt: String,
    val source: Source
)

data class Source(
    val name: String
)
