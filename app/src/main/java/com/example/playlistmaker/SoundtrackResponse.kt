package com.example.playlistmaker


// объект ответа содержит 2 сущности: инт и массив с треками - этот ответ взял из требований спецификации ответа апи
class SoundtrackResponse (val resultCount: Int, val results: ArrayList<Track>)