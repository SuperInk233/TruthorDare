package com.example.truthordare

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoveDareActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var extractButton: Button
    private lateinit var backButton: TextView
    private lateinit var counterTextView: TextView

    private var isExtracting = false
    private var extractHandler = Handler(Looper.getMainLooper())
    private var scrollHandler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null

    private var scrollSpeed = 50
    private var isScrolling = false
    private var scrollCount = 0

    private lateinit var dareQuestions: List<String>

    private var availableQuestions = mutableListOf<String>()
    private var usedQuestions = mutableSetOf<String>()
    private var lastQuestion: String? = null
    private var usedCount = 0

    companion object {
        private const val PREFS_NAME = "love_dare_prefs"
        private const val KEY_USED_COUNT = "love_dare_used_count"
        private const val KEY_USED_QUESTIONS = "love_dare_used_questions"
        private const val KEY_LAST_QUESTION = "love_dare_last_question"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_love_dare)

        questionTextView = findViewById(R.id.questionTextView)
        extractButton = findViewById(R.id.extractButton)
        backButton = findViewById(R.id.backButton)
        counterTextView = findViewById(R.id.counterTextView)

        // ★★★★★ 从仓库获取题库，不强制重置 ★★★★★
        dareQuestions = QuestionRepository.getLoveDareQuestions(this)

        loadSavedState()
        updateCounter()

        backButton.setOnClickListener { finish() }

        extractButton.setOnClickListener {
            if (!isExtracting && !isScrolling) {
                startExtracting()
            } else if (isScrolling) {
                stopScrolling()
            }
        }
    }

    private fun loadSavedState() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        usedCount = prefs.getInt(KEY_USED_COUNT, 0)
        val json = prefs.getString(KEY_USED_QUESTIONS, null)
        if (json != null) {
            val type = object : TypeToken<MutableSet<String>>() {}.type
            usedQuestions = Gson().fromJson(json, type)
        }
        lastQuestion = prefs.getString(KEY_LAST_QUESTION, null)
        resetAvailableQuestions()
    }

    private fun saveState() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        prefs.putInt(KEY_USED_COUNT, usedCount)
        prefs.putString(KEY_USED_QUESTIONS, Gson().toJson(usedQuestions))
        prefs.putString(KEY_LAST_QUESTION, lastQuestion)
        prefs.apply()
    }

    private fun resetAvailableQuestions() {
        availableQuestions.clear()
        for (q in dareQuestions) {
            if (!usedQuestions.contains(q)) {
                availableQuestions.add(q)
            }
        }
        if (availableQuestions.isEmpty()) {
            usedQuestions.clear()
            usedCount = 0
            availableQuestions.addAll(dareQuestions)
            saveState()
        }
    }

    private fun startExtracting() {
        isExtracting = true
        extractButton.text = "停止"
        startQuestionScrolling()
        extractHandler.postDelayed({ stopScrollingAndShowResult() }, 3000)
    }

    private fun startQuestionScrolling() {
        isScrolling = true
        scrollCount = 0
        scrollSpeed = 50
        scrollRunnable = object : Runnable {
            override fun run() {
                if (isScrolling) {
                    questionTextView.text = dareQuestions.random()
                    scrollCount++
                    if (scrollCount > 30) scrollSpeed = 80
                    if (scrollCount > 45) scrollSpeed = 120
                    if (scrollCount > 52) scrollSpeed = 200
                    scrollHandler.postDelayed(this, scrollSpeed.toLong())
                }
            }
        }
        scrollHandler.post(scrollRunnable!!)
    }

    private fun stopScrollingAndShowResult() {
        isScrolling = false
        isExtracting = false
        extractButton.text = "抽取题目"
        val finalQuestion = getUniqueRandomQuestion()
        questionTextView.text = finalQuestion
        usedCount++
        saveHistory(finalQuestion, "心动大冒险")
        saveState()
        updateCounter()
        extractHandler.removeCallbacksAndMessages(null)
        scrollHandler.removeCallbacksAndMessages(null)
    }

    private fun stopScrolling() {
        isScrolling = false
        isExtracting = false
        extractButton.text = "抽取题目"
        val finalQuestion = getUniqueRandomQuestion()
        questionTextView.text = finalQuestion
        usedCount++
        saveHistory(finalQuestion, "心动大冒险")
        saveState()
        updateCounter()
        extractHandler.removeCallbacksAndMessages(null)
        scrollHandler.removeCallbacksAndMessages(null)
    }

    private fun getUniqueRandomQuestion(): String {
        if (availableQuestions.isEmpty()) resetAvailableQuestions()
        var randomQuestion: String
        var attempts = 0
        do {
            randomQuestion = availableQuestions.random()
            attempts++
            if (attempts > 50) break
        } while (usedQuestions.contains(randomQuestion) && randomQuestion != lastQuestion)
        usedQuestions.add(randomQuestion)
        lastQuestion = randomQuestion
        availableQuestions.remove(randomQuestion)
        return randomQuestion
    }

    private fun updateCounter() {
        counterTextView.text = "$usedCount/${dareQuestions.size}"
    }

    override fun onDestroy() {
        super.onDestroy()
        extractHandler.removeCallbacksAndMessages(null)
        scrollHandler.removeCallbacksAndMessages(null)
    }

    private fun saveHistory(question: String, type: String) {
        val prefs = getSharedPreferences("TruthOrDarePrefs", MODE_PRIVATE)
        val historySet = prefs.getStringSet("history", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val timestamp = System.currentTimeMillis()
        val historyItem = "$timestamp|$type|$question"
        historySet.add(historyItem)
        prefs.edit().putStringSet("history", historySet).apply()
    }
}