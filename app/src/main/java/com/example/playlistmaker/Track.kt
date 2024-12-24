package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackName") val trackName: String, // Название композиции // для тренировки и на всякий - сериализованное имя аннотирую
    @SerializedName("artistName") val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, //  Продолжительность трека - для формата из цифры в строку поменял на Лонг
    val artworkUrl100: String? // Ссылка на изображение обложки; может не прийти
    )