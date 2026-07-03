package com.example.truthordare

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoveDareActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var extractButton: Button
    private lateinit var counterTextView: TextView
    private lateinit var backToNormalBtn: TextView

    private var usedCount = 0
    private var usedQuestions = mutableListOf<String>()
    private var lastQuestion = ""
    private var dareQuestions = mutableListOf<String>()

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
        counterTextView = findViewById(R.id.counterTextView)
        backToNormalBtn = findViewById(R.id.backToNormalBtn)

        dareQuestions = QuestionRepository.getLoveDareQuestions(this).toMutableList()

        loadSavedState()
        updateCounter()

        extractButton.setOnClickListener {
            extractQuestion()
        }

        backToNormalBtn.setOnClickListener {
            finish()
        }
    }

    private fun extractQuestion() {
        val available = dareQuestions.filter { it !in usedQuestions && it != lastQuestion }
        if (available.isEmpty()) {
            questionTextView.text = "所有题目已用完，请重置或添加新题"
            return
        }
        val randomIndex = (0 until available.size).random()
        val selected = available[randomIndex]
        questionTextView.text = selected
        usedCount++
        usedQuestions.add(selected)
        lastQuestion = selected
        saveState()
        updateCounter()
    }

    private fun updateCounter() {
        val remaining = dareQuestions.size - usedCount
        counterTextView.text = "剩余：$remaining 题"
    }

    private fun loadSavedState() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        usedCount = prefs.getInt(KEY_USED_COUNT, 0)
        val json = prefs.getString(KEY_USED_QUESTIONS, null)
        usedQuestions = if (json != null) {
            Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
        } else {
            mutableListOf()
        }
        lastQuestion = prefs.getString(KEY_LAST_QUESTION, "") ?: ""
    }

    private fun saveState() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(KEY_USED_COUNT, usedCount)
        editor.putString(KEY_USED_QUESTIONS, Gson().toJson(usedQuestions))
        editor.putString(KEY_LAST_QUESTION, lastQuestion)
        editor.apply()
    }
}