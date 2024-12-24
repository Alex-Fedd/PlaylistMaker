package com.example.playlistmaker

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: Long, //  Продолжительность трека - для формата из цифры в строку поменял на Лонг
    val artworkUrl100: String? // Ссылка на изображение обложки; может не прийти
    )