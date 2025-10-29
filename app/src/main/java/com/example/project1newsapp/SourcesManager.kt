package com.example.project1newsapp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class SourcesManager {
    val okHttpClient: OkHttpClient

    init{
        val builder= OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient=builder.build()
    }

    suspend fun retrieveSources(category: String,
                              apikey: String): List<Sources> {
        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines/sources?category=$category")
            .header("authorization", "Bearer $apikey")
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sourcesList = mutableListOf<Sources>()
            val json = JSONObject(responseBody)
            val sources = json.getJSONArray("sources")
            for (i in 0 until sources.length()) {
                val currSource = sources.getJSONObject(i)
                val id = currSource.getString("id")
                val name = currSource.getString("name")
                val description = currSource.getString("description")
                val url = currSource.getString("url")
                val category = currSource.getString("category")
                val language = currSource.getString("language")
                val country = currSource.getString("country")


                val source = Sources(
                    id = id,
                    name = name,
                    description = description,
                    category = category

                )

                sourcesList.add(source)
            }

            return sourcesList

        } else {
            return listOf()
        }
    }
}