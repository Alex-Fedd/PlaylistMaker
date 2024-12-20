package com.example.playlistmaker

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Search : AppCompatActivity() {

    private var searchInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<Button>(R.id.back_search_button)
        backButton.setOnClickListener {
            this.finish()
        }

        val inputET = findViewById<EditText>(R.id.search_field)

        val clearTextButton = findViewById<ImageView>(R.id.clear_btn)
        clearTextButton.setOnClickListener{
            inputET.getText().clear()  // можно и .setText("")
            hideKeyB(inputET)
        }

        val linearLay = findViewById<LinearLayout>(R.id.search_activity)
        linearLay.setOnClickListener {
            hideKeyB(linearLay) // если нажал вне поля ввода - исчезает клавиатура
        }

        //inputET.requestFocus() // фокус при нажатии на Эдит, но здесь не нужен
        inputET.addTextChangedListener(
            object:TextWatcher{
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!s.isNullOrEmpty()){ // если ввел что-то - иконка очистки есть
                        if(isDarkModeOn()){ // если в ночном, цвет текста черный
                            inputET.setTextColor(Color.BLACK)
                            clearTextButton.visibility = clearButtonVisible(s)
                        } else {
                            clearTextButton.visibility = clearButtonVisible(s)
                        }

                    } else {
                        clearTextButton.visibility = clearButtonVisible(s) // если не ввел - убрать видимость иконки очистки
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    searchInput = s.toString() // когда уже кончили вводить - это и кладем в глобал.переменную
                }

            })

            // слушатель проверки нажатия на клав.done/enter/ввода - для удаления фокуса(курсора) и скрытия клавиатуры
        inputET.setOnEditorActionListener{ _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyB(inputET)
                true
            } else {
                false
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_INPUT, "")

        //setText() не нужен - и так сохраняется и вставляется текст при поворотах
    }

    private fun clearButtonVisible(s: CharSequence?):Int{ // проверка, если текст пустой,
                                                          // делать невидимым кнопку очистки текста, и наоборот
        return if(s.isNullOrEmpty()){
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyB(v:View){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
    }

    private fun isDarkModeOn(): Boolean { // нашел фун, как проверить, включен ли ночной режим
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

    companion object{
        const val SEARCH_INPUT = "SEARCH INPUT"
    }

}