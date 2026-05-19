package com.example.truthordare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var backButton: TextView
    private lateinit var clearButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // 初始化视图
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        backButton = findViewById(R.id.backButton)
        clearButton = findViewById(R.id.clearButton)

        // 设置返回按钮
        backButton.setOnClickListener {
            finish()
        }

        // 设置清空按钮
        clearButton.setOnClickListener {
            showClearConfirmationDialog()
        }

        // 加载历史记录
        loadHistory()
    }

    private fun loadHistory() {
        val mainActivity = this as? MainActivity
        val history = mainActivity?.getHistory() ?: emptyList()

        if (history.isEmpty()) {
            Toast.makeText(this, "暂无历史记录", Toast.LENGTH_SHORT).show()
        } else {
            historyRecyclerView.layoutManager = LinearLayoutManager(this)
            historyRecyclerView.adapter = HistoryAdapter(history)
        }
    }

    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("确认清空")
            .setMessage("确定要清空所有历史记录吗？")
            .setPositiveButton("清空") { dialog, which ->
                clearHistory()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun clearHistory() {
        val mainActivity = this as? MainActivity
        mainActivity?.clearHistory()
        Toast.makeText(this, "历史记录已清空", Toast.LENGTH_SHORT).show()
        loadHistory() // 重新加载
    }
}

// 历史记录适配器
class HistoryAdapter(private val historyList: List<Triple<Long, String, String>>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.timeText)
        val typeText: TextView = view.findViewById(R.id.typeText)
        val questionText: TextView = view.findViewById(R.id.questionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (timestamp, type, question) = historyList[position]

        // 格式化时间
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        holder.timeText.text = sdf.format(date)

        // 设置类型
        holder.typeText.text = type

        // 设置题目
        holder.questionText.text = question
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}