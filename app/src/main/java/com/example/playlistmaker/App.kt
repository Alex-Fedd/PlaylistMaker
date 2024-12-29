package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


// топ левел константы
const val SHARED_PREF_KEY = "SHARED_PREF_KEY"
const val SWITCHER_KEY = "SWITCHER_KEY"

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPref = getSharedPreferences(SHARED_PREF_KEY, MODE_PRIVATE) // достал ШП, дал ему имя в локальном хранилище телефона

        if(!sharedPref.contains(SWITCHER_KEY)) {//проверка условия, что в ШП есть ключ при старте прилки
            sharedPref.edit(). //если по ключу нчиего нет при старте - создаем булево значение текущей темы (метод определяет её), и сохраняем
            putBoolean(SWITCHER_KEY, isDarkThemeOn()).
            apply()
        }

        darkTheme = sharedPref.getBoolean(SWITCHER_KEY, false)//ставим флагу значение ключа темы, если есть
        switchTheme(darkTheme) //при старте апки собственно ставим тему последнюю запомненную
    }


    fun switchTheme(darkIsOn: Boolean){ // самостоятельно меняем тему по переданному флагу-значению (как из слушателя класса Сеттингс, так можно и здесь в классе при старте апки)
        darkTheme = darkIsOn // нашему индикатору Апп-класса передаем актуальное значение из аргумента
        val sharedPref = getSharedPreferences(SHARED_PREF_KEY, MODE_PRIVATE)
        sharedPref.edit().putBoolean(SWITCHER_KEY, darkIsOn).apply() // по ключу сохраняем текущее положение темы (свитчера в Сеттингс-классе или загруженного флага темы при старте апки)

        AppCompatDelegate.setDefaultNightMode(//ставим тему исходя из флага свитчера актуального
            if(darkIsOn) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }


    private fun isDarkThemeOn(): Boolean { // Как проверить, включен ли ночной режим
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

}