package com.example.project1newsapp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class HeadlinesManager {
    val okHttpClient: OkHttpClient

    init{
        val builder= OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient=builder.build()
    }

    suspend fun retrieveHeadlines(category: String, pageNum: Int,
                                apikey: String): Headlines {
        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?category=$category&page=$pageNum")
            .header("authorization", "Bearer $apikey")
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {

            val headlinesList = mutableListOf<Results>()
            val json = JSONObject(responseBody)

            val totalRequests = json.getInt("totalResults")
            val headlines = json.getJSONArray("articles")

            for (i in 0 until headlines.length()) {
                val currResult = headlines.getJSONObject(i)

                val source = currResult.getJSONObject("source")
                val sourceName = source.getString("name")
                val description = currResult.getString("description")
                val url = currResult.getString("url")
                val imageurl = currResult.getString("urlToImage")
                val author = currResult.getString("author")
                val title = currResult.getString("title")
                val content = currResult.getString("content")


                val result = Results(

                    name = sourceName,
                    description = description,
                    url = url,
                    imageUrl = imageurl,
                    title = title

                )
                headlinesList.add(result)
            }

            val maxPage = ((totalRequests+19)/20)
            return Headlines(headlinesList, maxPage)
        }
        else {
            return Headlines(listOf(), 0)
        }


    }
}