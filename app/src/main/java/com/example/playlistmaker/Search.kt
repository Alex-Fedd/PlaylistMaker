package com.example.playlistmaker

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class Search : AppCompatActivity() {

    private var searchInput: String = ""
    private val baseUrl = "https://itunes.apple.com"

    // добавил логинг по рекомендации - для мониторинга (запросы проходят)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
    private val retro = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val soundtrackApi = retro.create<SoundtrackApi>()

    private lateinit var tracksList: ArrayList<Track> // создаю ссылку на список, позже инициализирую список треков
    private lateinit var adapter: TracksAdapter
    private lateinit var placeholderErrorsArea: LinearLayout
    private lateinit var resetBtn: MaterialButton
    private lateinit var errorPhrase: MaterialTextView
    private lateinit var errorPic: ImageView
    private lateinit var clearTextButton: ImageView

    private var myRecentInput: String =
        "" // для запоминания последнего введенного и перезапроса по Кнопке ресета,
    // если она показывается при запросе

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ниже несколько строк про RV и адаптер
        tracksList = arrayListOf()
        adapter = TracksAdapter(tracksList) // создал адаптер и передал список в него
        val recyclerView = findViewById<RecyclerView>(R.id.rv_tracks) // нашел свой РВ в хмл
        recyclerView.adapter = adapter // сажаю адаптер свой на адаптер РВшки

        val backButton = findViewById<Toolbar>(R.id.toolbar_search_back)
        backButton.setNavigationOnClickListener {
            this.finish()
        }

        val inputET = findViewById<EditText>(R.id.search_field)

        clearTextButton = findViewById(R.id.clear_btn)
        clearTextButton.setOnClickListener {
            inputET.getText().clear()  // можно и .setText("")
            tracksList.clear()
            //placeholderErrorsArea.visibility = View.GONE
            adapter.notifyDataSetChanged()
            hideKeyB(inputET)
        }

        val linearLay = findViewById<LinearLayout>(R.id.search_activity)
        linearLay.setOnClickListener {
            hideKeyB(linearLay) // если нажал вне поля ввода - исчезает клавиатура
        }

        placeholderErrorsArea = findViewById(R.id.placeholder_area)
        resetBtn = findViewById(R.id.reset_btn)
        errorPhrase = findViewById(R.id.error_phrase)
        errorPic = findViewById(R.id.error_picture)


        //inputET.requestFocus() // фокус при нажатии на Эдит, но здесь не нужен
        inputET.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) { // если ввел что-то - иконка очистки есть
                        if (isDarkModeOn()) { // если в ночном, цвет текста черный
                            inputET.setTextColor(Color.BLACK)
                            clearTextButton.visibility = clearButtonVisible(s)
                        } else {
                            clearTextButton.visibility = clearButtonVisible(s)
                        }

                    } else {
                        clearTextButton.visibility =
                            clearButtonVisible(s) // если не ввел - убрать видимость иконки очистки
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    searchInput =
                        s.toString() // когда уже закончили вводить-то кладем в глобал.переменную
                    if (s.isNullOrEmpty()) {
                        errorPic.visibility = View.GONE
                        placeholderErrorsArea.visibility = View.GONE
                        errorPhrase.visibility = View.GONE
                        tracksList.clear()
                        adapter.notifyDataSetChanged()
                    }
                }

            })
        // слушатель проверки нажатия на клав.done - для удаления фокуса(курсора) и скрытия клавиатуры
        inputET.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyB(inputET)
                // здесь по нажатию done запрос в сеть с введённым текстом
                networkTrackSearch(inputET.text.toString().trim())
                true
            } else {
                false
            }
        }

        resetBtn.setOnClickListener { // при нажатии на обновить, ставлю прошлый свой текст и переделываю запрос
            inputET.setText(myRecentInput)
            networkTrackSearch(myRecentInput)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) { // при смене конфига (поворот) сохраняет введенные строки. Но при выходе из апки потом понадобится SharedPref*
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_INPUT, "")
        //setText() не нужен - и так сохраняется и вставляется текст при поворотах
    }

    private fun clearButtonVisible(s: CharSequence?): Int { // проверка, если текст пустой,
        // делать невидимым кнопку очистки текста, и наоборот
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyB(v: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
    }

    private fun isDarkModeOn(): Boolean { // нашел фун, как проверить, включен ли ночной режим
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
    }

    private fun networkTrackSearch(stringInput: String) {
        if (stringInput.isNotEmpty()) {
            if (stringInput.isBlank()) {
                Log.d("API_TRACKS", "Input is blank!")
                return
            }
            myRecentInput = stringInput // прихраниваю запрос текстовый последний
            soundtrackApi.search(stringInput).enqueue(object : Callback<SoundtrackResponse> {
                override fun onResponse(
                    call: Call<SoundtrackResponse>,
                    response: Response<SoundtrackResponse>
                ) {
                    Log.d("TRANSLATION_LOG", "Status code: ${response.code()}")
                    Log.d("API_RESPONSE", response.body()?.toString() ?: "No body")

                    if (response.isSuccessful) {
                        tracksList.clear() // если ответ успешен, то чищу список от прошлого и далее проверяю не пуст ли. Если ок - кладу в свой

                        if (response.body()?.results?.isNotEmpty() == true
                            && response.body()?.resultCount!! > 0
                        ) {
                            Log.d("API_TRACKS", response.body()!!.results.toString())
                            tracksList.addAll(response.body()?.results!!)
                            placeholderErrorsArea.visibility = View.GONE
                            adapter.notifyDataSetChanged()
                        }
                        if (tracksList.isEmpty()) {
                            placeholderErrorsArea.visibility = View.VISIBLE
                            errorPic.visibility = View.VISIBLE
                            errorPhrase.visibility = View.VISIBLE
                            errorPic.setImageResource(R.drawable.err)
                            errorPhrase.text = getString(R.string.nothing_was_found)
                            resetBtn.visibility = View.GONE
                            Log.d("API_TRACKS", "Empty list of tracks returned!!")
                            //showMessage(getString(R.string.nothing_found), "")
                        } else {
                            placeholderErrorsArea.visibility = View.GONE
                            resetBtn.visibility = View.GONE
                            Log.d("API_TRACKS", "Else case - the list is not empty")
                            //showMessage("","")
                        }

                    } else {
                        placeholderErrorsArea.visibility = View.VISIBLE
                        errorPic.visibility = View.VISIBLE
                        errorPhrase.visibility = View.VISIBLE
                        errorPic.setImageResource(R.drawable.err_network)
                        val textIssue = getString(R.string.network_issues)
                        errorPhrase.text = textIssue
                        resetBtn.visibility = View.VISIBLE
                        tracksList.clear()
                        adapter.notifyDataSetChanged()
                        Log.d("API_TRACKS", "Not 200 code but there was some response, maybe 400")
                    }

                }

                override fun onFailure(call: Call<SoundtrackResponse>, t: Throwable) {
                    tracksList.clear()
                    adapter.notifyDataSetChanged()
                    errorPic.visibility = View.VISIBLE //при инет-ошибке все поля плейсхолдеры видны
                    errorPhrase.visibility = View.VISIBLE
                    placeholderErrorsArea.visibility = View.VISIBLE
                    errorPic.setImageResource(R.drawable.err_network) // текст про инет-ошибку
                    val netIssueText = getString(R.string.network_issues)
                    errorPhrase.text = netIssueText
                    resetBtn.visibility = View.VISIBLE
                    Log.d("API_TRACKS", "onFailure - no network")
                }

            })
        }
    }

    companion object {
        const val SEARCH_INPUT = "SEARCH INPUT"
    }

}


// мок-список треков на всякий пока не удаляю:
//    private fun createTracksList(): ArrayList<Track> {
//        val tracksList: ArrayList<Track> = arrayListOf()
//        return tracksList.apply {
//            add(
//                Track(
//                    getString(R.string.track_1_tr_name),
//                    getString(R.string.track_1_artist_name),
//                    getString(R.string.track_1_tr_time),
//                    getString(R.string.track_1_artwork_url)
//                )
//            )
//        }
//    }
