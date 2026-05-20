package com.example.truthordare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 界面组件
    private lateinit var truthProgressText: TextView
    private lateinit var dareProgressText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化界面组件
        val truthButton: Button = findViewById(R.id.truthButton)
        val dareButton: Button = findViewById(R.id.dareButton)
        val resetButton: Button = findViewById(R.id.resetButton)
        val historyButton: Button = findViewById(R.id.historyButton)
        val questionManagerButton: Button = findViewById(R.id.questionManagerButton)

        truthProgressText = findViewById(R.id.truthProgressText)
        dareProgressText = findViewById(R.id.dareProgressText)

        // 更新进度显示
        updateProgress()

        // 设置按钮点击监听器
        truthButton.setOnClickListener {
            val intent = Intent(this, TruthActivity::class.java)
            startActivity(intent)
        }

        dareButton.setOnClickListener {
            val intent = Intent(this, DareActivity::class.java)
            startActivity(intent)
        }

        resetButton.setOnClickListener {
            // 重置游戏数据
            resetGameData()
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        questionManagerButton.setOnClickListener {
            val intent = Intent(this, QuestionManagerActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次返回主界面时更新进度
        updateProgress()
    }

    /**
     * 更新进度显示
     */
    private fun updateProgress() {
        val prefs = getSharedPreferences("TruthOrDarePrefs", Context.MODE_PRIVATE)
        val truthUsed = prefs.getInt("truth_used_count", 0)
        val dareUsed = prefs.getInt("dare_used_count", 0)

        // 动态获取题库数量
        val truthTotal = QuestionRepository.getTruthQuestions(this).size
        val dareTotal = QuestionRepository.getDareQuestions(this).size

        truthProgressText.text = "$truthUsed/$truthTotal"
        dareProgressText.text = "$dareUsed/$dareTotal"
    }

    /**
     * 获取已使用的题目计数
     */
    fun getUsedCount(type: String): Int {
        val prefs = getSharedPreferences("TruthOrDarePrefs", Context.MODE_PRIVATE)
        return when (type) {
            "truth" -> prefs.getInt("truth_used_count", 0)
            "dare" -> prefs.getInt("dare_used_count", 0)
            else -> 0
        }
    }

    /**
     * 获取历史记录
     */
    fun getHistory(): List<Triple<Long, String, String>> {
        val prefs = getSharedPreferences("TruthOrDarePrefs", Context.MODE_PRIVATE)
        val historySet = prefs.getStringSet("history", setOf()) ?: setOf()

        val result = mutableListOf<Triple<Long, String, String>>()
        for (item in historySet) {
            val parts = item.split("|")
            if (parts.size == 3) {
                val timestamp = parts[0].toLongOrNull() ?: 0L
                val type = parts[1]
                val question = parts[2]
                result.add(Triple(timestamp, type, question))
            }
        }
        return result.sortedByDescending { it.first }
    }

    /**
     * 清除历史记录
     */
    fun clearHistory() {
        val prefs = getSharedPreferences("TruthOrDarePrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove("history")
        editor.apply()
    }

    /**
     * 重置游戏数据
     */
    private fun resetGameData() {
        val prefs = getSharedPreferences("TruthOrDarePrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        // 重置已使用计数
        editor.putInt("truth_used_count", 0)
        editor.putInt("dare_used_count", 0)
        // 重置已使用题目列表
        editor.remove("truth_used_questions")
        editor.remove("dare_used_questions")
        // 重置上次题目
        editor.remove("truth_last_question")
        editor.remove("dare_last_question")
        editor.apply()
        // 更新显示
        updateProgress()
        // 清除历史记录
        clearHistory()
        // 重置游戏到默认状态
        Toast.makeText(this, "游戏数据已重置", Toast.LENGTH_SHORT).show()
    }
}