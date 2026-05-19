package com.example.truthordare

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

/**
 * 批量添加对话框
 * 用于批量添加真心话/大冒险题目
 */
class BatchAddDialog(context: Context) : Dialog(context) {

    private lateinit var editText: EditText
    private lateinit var addButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_batch_add)

        // 初始化视图组件
        initViews()

        // 设置按钮点击事件
        setupClickListeners()
    }

    /**
     * 初始化视图组件
     */
    private fun initViews() {
        editText = findViewById(R.id.editTextBatch)
        addButton = findViewById(R.id.buttonAdd)
        cancelButton = findViewById(R.id.buttonCancel)
    }

    /**
     * 设置按钮点击事件
     */
    private fun setupClickListeners() {
        // 添加按钮点击事件
        addButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotBlank()) {
                // TODO: 这里添加批量添加题目的逻辑
                Toast.makeText(context, "批量添加功能开发中...", Toast.LENGTH_SHORT).show()
                dismiss() // 关闭对话框
            } else {
                Toast.makeText(context, "请输入内容", Toast.LENGTH_SHORT).show()
            }
        }

        // 取消按钮点击事件
        cancelButton.setOnClickListener {
            dismiss() // 关闭对话框
        }
    }
}