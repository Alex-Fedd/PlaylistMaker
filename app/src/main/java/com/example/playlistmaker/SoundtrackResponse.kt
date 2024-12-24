package com.example.playlistmaker

import com.google.gson.annotations.SerializedName


// объект ответа содержит 2 сущности: инт и массив с треками
class SoundtrackResponse (val resultCount: Int, val results: ArrayList<Track>)