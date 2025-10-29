package com.example.project1newsapp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class LocalManager {
    val okHttpClient: OkHttpClient

    init{
        val builder= OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient=builder.build()
    }

    suspend fun retrieveLocalNews(addressInfo: String,
                                apikey: String): List<Results> {
        val request = Request.Builder()
            .url("https://newsapi.org/v2/everything?q=$addressInfo&searchIn=Title")
            .header("authorization", "Bearer $apikey")
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val resultsList = mutableListOf<Results>()
            val json = JSONObject(responseBody)
            val results = json.getJSONArray("articles")
            for (i in 0 until results.length()) {
                val currResult = results.getJSONObject(i)

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

                resultsList.add(result)
            }

            return resultsList

        } else {
            return listOf()
        }
    }
}