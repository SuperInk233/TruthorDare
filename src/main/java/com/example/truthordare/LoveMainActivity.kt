package com.example.truthordare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoveMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_love_main)

        // 真心话按钮 -> 跳转到心动真心话
        val loveTruthButton: Button = findViewById(R.id.loveTruthButton)
        loveTruthButton.setOnClickListener {
            startActivity(Intent(this, LoveTruthActivity::class.java))
        }

        // 大冒险按钮 -> 跳转到心动大冒险
        val loveDareButton: Button = findViewById(R.id.loveDareButton)
        loveDareButton.setOnClickListener {
            startActivity(Intent(this, LoveDareActivity::class.java))
        }

        // 返回普通模式
        val backToNormalBtn: TextView = findViewById(R.id.backToNormalBtn)
        backToNormalBtn.setOnClickListener {
            finish()
        }

        // 底部三个按钮（暂不实现功能，可留空或提示）
        val loveResetButton: Button = findViewById(R.id.loveResetButton)
        loveResetButton.setOnClickListener {
            // 可以在这里实现重置心动数据（留空或提示）
        }

        val loveHistoryButton: Button = findViewById(R.id.loveHistoryButton)
        loveHistoryButton.setOnClickListener {
            // 可以跳转到心动历史（暂未实现）
        }

        val loveQuestionManagerButton: Button = findViewById(R.id.loveQuestionManagerButton)
        loveQuestionManagerButton.setOnClickListener {
            // 可以跳转到心动题库管理（暂未实现）
        }
    }
}