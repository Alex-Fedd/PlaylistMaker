package com.example.playlistmaker


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SoundtrackApi {
    @GET("/search")
    fun search(@Query("term") song: String): Call<SoundtrackResponse>
}
// аннотировал ГЕТ + эндпойнт с путем
// квири параметр передаю в запросе и достраиваю так урл для фильтрации по конкретному запросу строковому
// + импортнул класс дженерик для ответа
