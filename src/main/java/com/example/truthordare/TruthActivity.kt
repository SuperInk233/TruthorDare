package com.example.truthordare

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TruthActivity : AppCompatActivity() {

    // 界面组件
    private lateinit var questionTextView: TextView
    private lateinit var extractButton: Button
    private lateinit var backButton: TextView
    private lateinit var titleTextView: TextView
    private lateinit var counterTextView: TextView  // 新增：计数器

    // 抽取相关变量
    private var isExtracting = false
    private var extractHandler = Handler(Looper.getMainLooper())
    private var extractRunnable: Runnable? = null
    private var scrollHandler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null

    // 滚动动画相关
    private var scrollPosition = 0
    private var scrollSpeed = 50 // 初始滚动速度(ms)
    private var isScrolling = false
    private var scrollCount = 0
    private val maxScrollCount = 60 // 大约滚动3秒(3000ms/50ms)

    // 题目列表 - 从QuestionRepository获取
    private lateinit var truthQuestions: List<String>

    // 优化随机抽取的关键变量
    private var availableQuestions = mutableListOf<String>()  // 可用题目列表
    private var usedQuestions = mutableSetOf<String>()       // 已使用题目集合
    private var lastQuestion: String? = null                  // 上次抽取的题目
    private var usedCount = 0                                // 已使用的题目数量

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truth)

        // 从QuestionRepository获取题目列表（必须在initViews之前）
        truthQuestions = QuestionRepository.getTruthQuestions(this)

        // 初始化界面组件
        initViews()

        // 加载已保存的状态
        loadSavedState()

        // 设置返回按钮点击事件
        backButton.setOnClickListener {
            finish() // 返回上一个界面
        }

        // 设置抽取按钮点击事件
        extractButton.setOnClickListener {
            if (!isExtracting && !isScrolling) {
                startExtracting() // 开始抽取
            } else if (isScrolling) {
                // 如果在滚动中，点击立即停止
                stopScrolling()
            }
        }

        // 更新计数器显示
        updateCounter()
    }

    /**
     * 初始化界面组件
     */
    private fun initViews() {
        questionTextView = findViewById(R.id.questionTextView)
        extractButton = findViewById(R.id.extractButton)
        backButton = findViewById(R.id.backButton)
        titleTextView = findViewById(R.id.titleTextView)
        counterTextView = findViewById(R.id.counterTextView)  // 新增

        // 初始化可用题目列表
        resetAvailableQuestions()
    }

    /**
     * 加载已保存的状态
     */
    private fun loadSavedState() {
        val prefs = getSharedPreferences("TruthOrDarePrefs", MODE_PRIVATE)

        // 获取已使用计数
        usedCount = prefs.getInt("truth_used_count", 0)

        // 获取已使用题目列表
        val usedQuestionsJson = prefs.getString("truth_used_questions", null)
        if (usedQuestionsJson != null) {
            val type = object : TypeToken<MutableSet<String>>() {}.type
            usedQuestions = Gson().fromJson(usedQuestionsJson, type)
        }

        // 获取上次题目
        lastQuestion = prefs.getString("truth_last_question", null)

        // 重置可用题目列表（排除已使用的题目）
        resetAvailableQuestions()
    }

    /**
     * 保存当前状态
     */
    private fun saveState() {
        val prefs = getSharedPreferences("TruthOrDarePrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        // 保存已使用计数
        editor.putInt("truth_used_count", usedCount)

        // 保存已使用题目列表
        val usedQuestionsJson = Gson().toJson(usedQuestions)
        editor.putString("truth_used_questions", usedQuestionsJson)

        // 保存上次题目
        editor.putString("truth_last_question", lastQuestion)

        editor.apply()
    }

    /**
     * 重置可用题目列表
     * 当所有题目都被使用后，重置列表重新开始
     */
    private fun resetAvailableQuestions() {
        availableQuestions.clear()

        // 从所有题目中排除已使用的题目
        for (question in truthQuestions) {
            if (!usedQuestions.contains(question)) {
                availableQuestions.add(question)
            }
        }

        // 如果没有可用题目了，清空已使用列表重新开始
        if (availableQuestions.isEmpty()) {
            usedQuestions.clear()
            usedCount = 0
            availableQuestions.addAll(truthQuestions)
            saveState()
        }
    }

    /**
     * 开始抽取题目
     */
    private fun startExtracting() {
        isExtracting = true
        extractButton.text = "停止" // 修改按钮文字

        // 开始快速滚动动画
        startQuestionScrolling()

        // 3秒后停止滚动并显示最终题目
        extractHandler.postDelayed({
            stopScrollingAndShowResult()
        }, 3000)
    }

    /**
     * 开始题目快速滚动
     */
    private fun startQuestionScrolling() {
        isScrolling = true
        scrollPosition = 0
        scrollCount = 0
        scrollSpeed = 50 // 初始速度

        scrollRunnable = object : Runnable {
            override fun run() {
                if (isScrolling) {
                    // 显示随机题目
                    val randomQuestion = getRandomQuestionForScroll()
                    questionTextView.text = randomQuestion

                    scrollCount++

                    // 逐渐减慢滚动速度
                    if (scrollCount > 30) { // 1.5秒后开始减速
                        scrollSpeed = 80
                    }
                    if (scrollCount > 45) { // 2.25秒后更慢
                        scrollSpeed = 120
                    }
                    if (scrollCount > 52) { // 2.6秒后最慢
                        scrollSpeed = 200
                    }

                    // 继续滚动
                    scrollHandler.postDelayed(this, scrollSpeed.toLong())
                }
            }
        }

        scrollHandler.post(scrollRunnable!!)
    }

    /**
     * 获取用于滚动的随机题目
     */
    private fun getRandomQuestionForScroll(): String {
        return truthQuestions.random()
    }

    /**
     * 停止滚动并显示最终结果
     */
    private fun stopScrollingAndShowResult() {
        isScrolling = false
        isExtracting = false
        extractButton.text = "抽取题目"

        // 获取最终题目
        val finalQuestion = getUniqueRandomQuestion()
        questionTextView.text = finalQuestion

        // 增加已使用计数
        usedCount++

        // 保存历史记录
        saveHistory(finalQuestion, "真心话")

        // 保存当前状态
        saveState()

        // 更新计数器
        updateCounter()

        // 移除可能存在的后续任务
        extractHandler.removeCallbacksAndMessages(null)
        scrollHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 手动停止滚动
     */
    private fun stopScrolling() {
        isScrolling = false
        isExtracting = false
        extractButton.text = "抽取题目"

        // 获取最终题目
        val finalQuestion = getUniqueRandomQuestion()
        questionTextView.text = finalQuestion

        // 增加已使用计数
        usedCount++

        // 保存历史记录
        saveHistory(finalQuestion, "真心话")

        // 保存当前状态
        saveState()

        // 更新计数器
        updateCounter()

        // 移除可能存在的后续任务
        extractHandler.removeCallbacksAndMessages(null)
        scrollHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 获取不重复的随机题目
     * 优化算法：避免重复，直到所有题目都被使用
     */
    private fun getUniqueRandomQuestion(): String {
        // 如果可用题目列表为空，重置列表
        if (availableQuestions.isEmpty()) {
            resetAvailableQuestions()
        }

        var randomQuestion: String
        var attempts = 0

        do {
            // 从可用题目列表中随机选择
            randomQuestion = availableQuestions.random()
            attempts++

            // 避免无限循环，如果尝试次数过多，直接返回
            if (attempts > 50) {
                break
            }
        } while (usedQuestions.contains(randomQuestion) && randomQuestion != lastQuestion)

        // 标记为已使用
        usedQuestions.add(randomQuestion)
        lastQuestion = randomQuestion

        // 从可用列表中移除已使用的题目
        availableQuestions.remove(randomQuestion)

        return randomQuestion
    }

    /**
     * 更新计数器显示
     */
    private fun updateCounter() {
        val total = truthQuestions.size
        counterTextView.text = "$usedCount/$total"
    }

    /**
     * 避免内存泄漏，在Activity销毁时移除Handler任务
     */
    override fun onDestroy() {
        super.onDestroy()
        extractHandler.removeCallbacksAndMessages(null)
        scrollHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 保存历史记录
     */
    private fun saveHistory(question: String, type: String) {
        val prefs = getSharedPreferences("TruthOrDarePrefs", MODE_PRIVATE)
        val historySet = prefs.getStringSet("history", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val timestamp = System.currentTimeMillis()
        val historyItem = "$timestamp|$type|$question"
        historySet.add(historyItem)

        val editor = prefs.edit()
        editor.putStringSet("history", historySet)
        editor.apply()
    }
}