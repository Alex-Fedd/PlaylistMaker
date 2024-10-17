package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener { // оставил 1 анон.класс
            override fun onClick(v: View?) {
                val searchButtonIntent = Intent(this@MainActivity, Search::class.java)
                startActivity(searchButtonIntent)
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)

        val mediaButton = findViewById<Button>(R.id.mediateka_button)
        mediaButton.setOnClickListener{
            val mediaButtonIntent = Intent(this@MainActivity, Mediateka::class.java)
            startActivity(mediaButtonIntent)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener{
            val settingsButtonIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
        }


    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
}

