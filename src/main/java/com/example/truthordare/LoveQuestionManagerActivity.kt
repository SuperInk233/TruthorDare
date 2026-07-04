package com.example.truthordare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoveQuestionManagerActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var tabTruth: Button
    private lateinit var tabDare: Button
    private lateinit var questionsRecyclerView: RecyclerView
    private lateinit var addQuestionButton: Button

    private var currentTab = "truth"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_manager)

        backButton = findViewById(R.id.backButton)
        tabTruth = findViewById(R.id.tabTruth)
        tabDare = findViewById(R.id.tabDare)
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView)
        addQuestionButton = findViewById(R.id.addQuestionButton)

        backButton.setOnClickListener { finish() }

        tabTruth.setOnClickListener {
            currentTab = "truth"
            refreshDisplay()
        }

        tabDare.setOnClickListener {
            currentTab = "dare"
            refreshDisplay()
        }

        addQuestionButton.setOnClickListener { showAddQuestionDialog() }

        // 首次显示
        refreshDisplay()
    }

    override fun onResume() {
        super.onResume()
        refreshDisplay()
    }

    /**
     * 每次都从仓库重新获取数据并刷新界面
     */
    private fun refreshDisplay() {
        if (currentTab == "truth") {
            tabTruth.textSize = 18f
            tabTruth.setTextColor(getColor(android.R.color.black))
            tabDare.textSize = 16f
            tabDare.setTextColor(getColor(android.R.color.darker_gray))
            val questions = QuestionRepository.getLoveTruthQuestions(this)
            loadQuestions(questions, "心动真心话")
        } else {
            tabTruth.textSize = 16f
            tabTruth.setTextColor(getColor(android.R.color.darker_gray))
            tabDare.textSize = 18f
            tabDare.setTextColor(getColor(android.R.color.black))
            val questions = QuestionRepository.getLoveDareQuestions(this)
            loadQuestions(questions, "心动大冒险")
        }
    }

    private fun loadQuestions(questions: List<String>, type: String) {
        val questionItems = questions.mapIndexed { index, question ->
            QuestionItem(index + 1, question, type)
        }
        val adapter = QuestionAdapter(questionItems, this::onQuestionDeleted)
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = adapter
    }

    private fun onQuestionDeleted(question: String, type: String) {
        if (type == "心动真心话") {
            QuestionRepository.deleteLoveTruthQuestion(this, question)
        } else {
            QuestionRepository.deleteLoveDareQuestion(this, question)
        }
        refreshDisplay()
        Toast.makeText(this, "题目已删除", Toast.LENGTH_SHORT).show()
    }

    private fun showAddQuestionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_question, null)
        val editText = dialogView.findViewById<EditText>(R.id.questionEditText)
        editText.hint = "可输入单个或多个题目，支持以下分隔方式：\n1. 换行分隔\n2. 逗号分隔（,）\n3. 分号分隔（;）\n4. 顿号分隔（、）\n5. 句号分隔（。）"

        AlertDialog.Builder(this)
            .setTitle("添加题目")
            .setView(dialogView)
            .setPositiveButton("添加") { _, _ ->
                val inputText = editText.text.toString().trim()
                if (inputText.isNotBlank()) {
                    val questions = splitAndParseQuestions(inputText)
                    if (questions.isNotEmpty()) {
                        var successCount = 0
                        var duplicateCount = 0

                        // 从仓库获取当前列表用于查重
                        val currentList = if (currentTab == "truth") {
                            QuestionRepository.getLoveTruthQuestions(this)
                        } else {
                            QuestionRepository.getLoveDareQuestions(this)
                        }

                        for (question in questions) {
                            val trimmedQuestion = question.trim()
                            if (trimmedQuestion.isNotBlank()) {
                                val isDuplicate = currentList.any { it == trimmedQuestion }
                                if (!isDuplicate) {
                                    if (currentTab == "truth") {
                                        QuestionRepository.addLoveTruthQuestion(this, trimmedQuestion)
                                    } else {
                                        QuestionRepository.addLoveDareQuestion(this, trimmedQuestion)
                                    }
                                    successCount++
                                } else {
                                    duplicateCount++
                                }
                            }
                        }

                        // 添加后立即刷新
                        refreshDisplay()

                        val message = buildString {
                            append("成功添加 $successCount 个题目")
                            if (duplicateCount > 0) append("，跳过 $duplicateCount 个重复题目")
                        }
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "未识别到有效题目", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "请输入题目内容", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun splitAndParseQuestions(input: String): List<String> {
        val lines = input.split("\n")
        val questions = mutableListOf<String>()
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isBlank()) continue
            val separators = arrayOf(",", ";", "、", "。", "？", "！")
            var hasSubSeparator = false
            for (separator in separators) {
                if (trimmedLine.contains(separator)) {
                    val subParts = trimmedLine.split(separator)
                    for (part in subParts) {
                        val trimmedPart = part.trim()
                        if (trimmedPart.isNotBlank()) questions.add(trimmedPart)
                    }
                    hasSubSeparator = true
                    break
                }
            }
            if (!hasSubSeparator) questions.add(trimmedLine)
        }
        return questions
    }

    data class QuestionItem(val number: Int, val question: String, val type: String)

    class QuestionAdapter(
        private var questions: List<QuestionItem>,
        private val onDelete: (String, String) -> Unit
    ) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val numberText: TextView = view.findViewById(R.id.numberText)
            val questionText: TextView = view.findViewById(R.id.questionText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val question = questions[position]
            holder.numberText.text = "${question.number}."
            holder.questionText.text = question.question
            holder.itemView.setOnClickListener {
                showDeleteConfirmationDialog(holder.itemView.context, question.question, question.type)
            }
        }

        override fun getItemCount(): Int = questions.size

        private fun showDeleteConfirmationDialog(context: android.content.Context, question: String, type: String) {
            AlertDialog.Builder(context)
                .setTitle("删除确认")
                .setMessage("确定要删除这个题目吗？")
                .setPositiveButton("删除") { _, _ -> onDelete(question, type) }
                .setNegativeButton("取消", null)
                .show()
        }
    }
}