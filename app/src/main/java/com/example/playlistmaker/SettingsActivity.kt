package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            this.finish()
        }

        val shareTheAppButton = findViewById<ImageView>(R.id.share_app_button)
        shareTheAppButton.setOnClickListener {
            val shareAppLinkText = getString(R.string.course_website_link)
            val shareAppIntent = Intent()              // НБ: можно было через apply+createChooser еще https://developer.android.com/training/sharing/send#kotlin
            shareAppIntent.action = Intent.ACTION_SEND
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareAppLinkText)
            shareAppIntent.type = "text/plain"
            val chooseIntent = Intent.createChooser(shareAppIntent, null)
            startActivity(chooseIntent)
        }

        val supportButton = findViewById<ImageView>(R.id.supp_button)
        supportButton.setOnClickListener{
            val emailSubject = getString(R.string.email_title)
            val emailMsg = getString(R.string.email_body)
            val emailTo = getString(R.string.email_link)
            val supportIntent = Intent().apply {  // scope-функцию применил
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, emailTo)
                putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                putExtra(Intent.EXTRA_TEXT, emailMsg)
            }
            startActivity(supportIntent)
        }

        val userAgreementButton = findViewById<ImageView>(R.id.user_agreement_button)
        userAgreementButton.setOnClickListener{
            val yandexLink = getString(R.string.agreement_link) // toUri() можно еще сразу
            val url = Uri.parse(yandexLink)
            val userAgreementButtonIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(userAgreementButtonIntent)
        }

    }
}