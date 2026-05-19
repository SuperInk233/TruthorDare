package com.example.truthordare

/**
 * 游戏数据管理类
 * 用于管理游戏的状态和数据
 */
class GameData {

    // 游戏历史记录
    private val gameHistory = mutableListOf<String>()

    // 当前游戏状态
    private var currentScore = 0
    private var isGameActive = false

    /**
     * 重置游戏状态
     */
    fun resetGame() {
        gameHistory.clear()
        currentScore = 0
        isGameActive = false

        // 这里可以添加更多的重置逻辑
        // 例如：重置题目列表、清除选择历史等
    }

    /**
     * 添加游戏历史记录
     * @param record 历史记录内容
     */
    fun addHistory(record: String) {
        gameHistory.add(record)
    }

    /**
     * 获取游戏历史记录
     * @return 历史记录列表
     */
    fun getHistory(): List<String> {
        return gameHistory.toList()
    }

    /**
     * 获取当前分数
     * @return 当前分数
     */
    fun getCurrentScore(): Int {
        return currentScore
    }

    /**
     * 增加分数
     * @param points 增加的分数
     */
    fun addScore(points: Int) {
        currentScore += points
    }

    /**
     * 检查游戏是否活跃
     * @return 游戏是否活跃
     */
    fun isGameActive(): Boolean {
        return isGameActive
    }

    /**
     * 开始游戏
     */
    fun startGame() {
        isGameActive = true
    }

    /**
     * 结束游戏
     */
    fun endGame() {
        isGameActive = false
    }
}