package com.example.truthordare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoveMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_love_main)

        // 真心话 -> 心动真心话
        findViewById<Button>(R.id.loveTruthButton).setOnClickListener {
            startActivity(Intent(this, LoveTruthActivity::class.java))
        }

        // 大冒险 -> 心动大冒险
        findViewById<Button>(R.id.loveDareButton).setOnClickListener {
            startActivity(Intent(this, LoveDareActivity::class.java))
        }

        // 返回普通模式
        findViewById<TextView>(R.id.backToNormalBtn).setOnClickListener { finish() }

        // 重置心动数据
        findViewById<Button>(R.id.loveResetButton).setOnClickListener {
            showResetConfirmationDialog()
        }

        // 历史记录
        findViewById<Button>(R.id.loveHistoryButton).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // 题库管理 -> 心动专属
        findViewById<Button>(R.id.loveQuestionManagerButton).setOnClickListener {
            startActivity(Intent(this, LoveQuestionManagerActivity::class.java))
        }

        updateMainCounter()
    }

    override fun onResume() {
        super.onResume()
        updateMainCounter() // 从其他页面返回时刷新
    }

    private fun updateMainCounter() {
        val loveTruthTotal = QuestionRepository.getLoveTruthQuestions(this).size
        val loveDareTotal = QuestionRepository.getLoveDareQuestions(this).size
        findViewById<TextView>(R.id.loveTruthProgressText).text = "0/$loveTruthTotal"
        findViewById<TextView>(R.id.loveDareProgressText).text = "0/$loveDareTotal"
    }

    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("重置心动模式")
            .setMessage("确定要清空心动模式的所有进度、历史和使用记录吗？\n（题库默认题目不会被删除）")
            .setPositiveButton("重置") { _, _ -> resetLoveData() }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun resetLoveData() {
        val prefs = getSharedPreferences("TruthOrDarePrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val allHistory = prefs.getStringSet("history", mutableSetOf()) ?: mutableSetOf()
        val filtered = allHistory.filterNot { it.contains("|心动") }.toMutableSet()
        editor.putStringSet("history", filtered).apply()

        getSharedPreferences("love_truth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("love_dare_prefs", Context.MODE_PRIVATE).edit().clear().apply()

        Toast.makeText(this, "心动模式已重置", Toast.LENGTH_SHORT).show()
        updateMainCounter()
    }
}