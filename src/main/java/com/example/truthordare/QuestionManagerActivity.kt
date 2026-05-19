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

class QuestionManagerActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var tabTruth: Button
    private lateinit var tabDare: Button
    private lateinit var questionsRecyclerView: RecyclerView
    private lateinit var addQuestionButton: Button

    private var currentTab = "truth" // 当前选中的选项卡

    // 题目列表 - 从QuestionRepository获取
    private lateinit var truthQuestions: MutableList<String>
    private lateinit var dareQuestions: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_manager)

        // 初始化题目列表
        truthQuestions = QuestionRepository.getTruthQuestions(this)
        dareQuestions = QuestionRepository.getDareQuestions(this)

        // 初始化视图
        backButton = findViewById(R.id.backButton)
        tabTruth = findViewById(R.id.tabTruth)
        tabDare = findViewById(R.id.tabDare)
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView)
        addQuestionButton = findViewById(R.id.addQuestionButton)

        // 设置返回按钮
        backButton.setOnClickListener {
            finish()
        }

        // 设置选项卡点击事件
        tabTruth.setOnClickListener {
            switchTab("truth")
        }

        tabDare.setOnClickListener {
            switchTab("dare")
        }

        // 设置添加按钮
        addQuestionButton.setOnClickListener {
            showAddQuestionDialog()
        }

        // 默认显示真心话题库
        switchTab("truth")
    }

    private fun switchTab(tab: String) {
        currentTab = tab

        // 更新选项卡样式
        if (tab == "truth") {
            tabTruth.textSize = 18f
            tabTruth.setTextColor(getColor(android.R.color.black))
            tabDare.textSize = 16f
            tabDare.setTextColor(getColor(android.R.color.darker_gray))

            // 加载真心话题库
            loadQuestions(truthQuestions, "真心话")
        } else {
            tabTruth.textSize = 16f
            tabTruth.setTextColor(getColor(android.R.color.darker_gray))
            tabDare.textSize = 18f
            tabDare.setTextColor(getColor(android.R.color.black))

            // 加载大冒险题库
            loadQuestions(dareQuestions, "大冒险")
        }
    }

    private fun loadQuestions(questions: List<String>, type: String) {
        val questionItems = questions.mapIndexed { index, question ->
            QuestionItem(index + 1, question, type)
        }

        val adapter = QuestionAdapter(
            questionItems,
            if (type == "真心话") truthQuestions else dareQuestions,
            this::onQuestionDeleted
        )

        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsRecyclerView.adapter = adapter
    }

    // 删除题目回调
    private fun onQuestionDeleted(question: String, type: String) {
        if (type == "真心话") {
            QuestionRepository.deleteTruthQuestion(this, question)
            truthQuestions.remove(question)
        } else {
            QuestionRepository.deleteDareQuestion(this, question)
            dareQuestions.remove(question)
        }
        // 重新加载当前选项卡
        switchTab(currentTab)
        Toast.makeText(this, "题目已删除", Toast.LENGTH_SHORT).show()
    }

    // 显示添加题目对话框
    private fun showAddQuestionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_question, null)
        val editText = dialogView.findViewById<EditText>(R.id.questionEditText)

        AlertDialog.Builder(this)
            .setTitle("添加题目")
            .setView(dialogView)
            .setPositiveButton("添加") { dialog, which ->
                val newQuestion = editText.text.toString()
                if (newQuestion.isNotBlank()) {
                    if (currentTab == "truth") {
                        QuestionRepository.addTruthQuestion(this, newQuestion)
                        truthQuestions.add(newQuestion)
                    } else {
                        QuestionRepository.addDareQuestion(this, newQuestion)
                        dareQuestions.add(newQuestion)
                    }
                    // 重新加载当前选项卡
                    switchTab(currentTab)
                    Toast.makeText(this, "题目已添加", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "题目不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    data class QuestionItem(val number: Int, val question: String, val type: String)

    class QuestionAdapter(
        private var questions: List<QuestionItem>,
        private val questionList: MutableList<String>,
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

            // 设置点击事件，点击题目时弹出删除确认对话框
            holder.itemView.setOnClickListener {
                showDeleteConfirmationDialog(holder.itemView.context, question.question, question.type, position)
            }
        }

        override fun getItemCount(): Int {
            return questions.size
        }

        /**
         * 显示删除确认对话框
         */
        private fun showDeleteConfirmationDialog(context: android.content.Context, question: String, type: String, position: Int) {
            AlertDialog.Builder(context)
                .setTitle("删除确认")
                .setMessage("确定要删除这个题目吗？")
                .setPositiveButton("删除") { dialog, which ->
                    // 调用删除回调
                    onDelete(question, type)
                }
                .setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}