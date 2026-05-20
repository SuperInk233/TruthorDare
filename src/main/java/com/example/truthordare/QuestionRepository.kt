package com.example.truthordare

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object QuestionRepository {

    private const val PREFS_NAME = "QuestionsPrefs"
    private const val KEY_TRUTH_QUESTIONS = "truth_questions"
    private const val KEY_DARE_QUESTIONS = "dare_questions"

    // 初始默认题目
    private val defaultTruthQuestions = listOf(
        "你理想的伴侣是什么样子的？详细描述一下。",
        "你对婚姻有什么看法？打算什么时候结婚？",
        "你的家庭背景是怎样的？和父母关系好吗？",
        "你的经济状况和未来规划是什么？",
        "你对生育有什么看法？想要几个孩子？",
        "你希望未来的生活是什么样子的？",
        "你对家务分配有什么看法？",
        "你和前任分手的原因是什么？",
        "你谈过几次恋爱？最长的持续了多久？",
        "你会在意伴侣的过去吗？",
        "你对彩礼/嫁妆有什么看法？",
        "你希望未来的居住地是哪里？",
        "你的兴趣爱好是什么？希望伴侣有相同爱好吗？",
        "你的性格特点是什么？优缺点分别是什么？",
        "你对伴侣的收入有什么要求？",
        "你希望和伴侣如何相处？",
        "你对浪漫的定义是什么？",
        "你觉得两个人在一起最重要的是什么？",
        "你会为了伴侣改变自己吗？",
        "你希望伴侣如何支持你的事业？",
        "你对伴侣的年龄、身高、外貌有什么要求？",
        "你觉得怎样才算门当户对？",
        "你如何处理和伴侣的争吵？",
        "你的消费观念是怎样的？",
        "你对未来的规划是怎样的？",
        "你最近一次发自内心的大笑是因为什么？",
        "你人生中最骄傲的成就是什么？",
        "你最大的恐惧是什么？为什么？",
        "如果给你三天时间做任何事，你会做什么？",
        "你最好的朋友是谁？你们是怎么认识的？",
        "你最后悔的事情是什么？如果重来你会怎么做？",
        "你最大的梦想是什么？实现了吗？",
        "你最喜欢自己身体的哪个部位？为什么？",
        "你最不喜欢的食物是什么？为什么？",
        "你小时候的梦想是什么？现在实现了吗？",
        "你最近学会的新技能是什么？",
        "你最想去的三个地方是哪里？",
        "你最喜欢的电影是什么？为什么？",
        "你最近在读什么书？有什么感受？",
        "你觉得自己最大的优点是什么？",
        "你觉得自己需要改进的地方是什么？",
        "你最难忘的旅行经历是什么？",
        "你最喜欢的音乐类型是什么？",
        "你如何应对压力？",
        "你最近做过的最有成就感的事情是什么？",
        "你最珍惜的礼物是什么？谁送的？",
        "你最想感谢的人是谁？为什么？",
        "你做过最疯狂的事情是什么？",
        "你最尴尬的经历是什么？",
        "你最好的品质是什么？",
        "你希望自己拥有什么超能力？",
        "如果你能改变世界的一件事，你会改变什么？",
        "你最喜欢的童年回忆是什么？",
        "你最大的兴趣爱好是什么？",
        "你最近学到的人生教训是什么？",
        "你最佩服的人是谁？为什么？",
        "你最近克服了什么困难？",
        "你最喜欢的季节是什么？为什么？",
        "你最近有什么新的发现？",
        "你最想培养什么习惯？",
        "你最近帮助了谁？怎么帮助的？",
        "你最想学但还没学的技能是什么？",
        "你最喜欢的放松方式是什么？",
        "你最近有什么让你开心的小事？",
        "你最想对10年前的自己说什么？",
        "你未来5年的目标是什么？",
        "你最喜欢的动物是什么？为什么？",
        "你最近有什么让你感动的经历？",
        "你最喜欢的颜色是什么？为什么？",
        "你最近做了什么让自己自豪的事情？",
        "你最喜欢的时间是什么时候？",
        "你最近有什么新的感悟？",
        "你最喜欢的运动是什么？",
        "你最近有什么让你惊喜的事情？",
        "你最喜欢的一句话是什么？",
        "你最近一次尝试的新事物是什么？",
        "你觉得最美好的瞬间是什么？",
        "你最想改变自己什么习惯？",
        "你最近有什么有趣的想法？",
        "你觉得生活最重要的是什么？",
        "你最想拥有什么才能？",
        "你最近有什么小确幸？",
        "你觉得最幸福的时刻是什么？",
        "你最近学到了什么新知识？",
        "你最想和谁一起旅行？",
        "你觉得最有意义的事情是什么？",
        "你最近有什么新爱好？",
        "你最想尝试什么冒险？",
        "你觉得最温暖的记忆是什么？",
        "你最近有什么突破？",
        "你最想体验什么职业？",
        "你觉得最有趣的事情是什么？",
        "你最近有什么成长？",
        "你最想实现什么愿望？",
        "你觉得最有价值的东西是什么？",
        "你最近有什么感动？",
        "你最想分享什么经历？",
        "你觉得最重要的品质是什么？",
        "你最近有什么发现？",
        "你最近一次心动是因为什么事情？",
        "你理想中的恋人是什么样子的？",
        "你曾经暗恋过谁？为什么喜欢TA？",
        "你谈过几次恋爱？最长的一段是多久？",
        "你手机里有没有不想被别人看到的照片？",
        "你做过最疯狂的事情是什么？",
        "你觉得自己最大的缺点是什么？",
        "如果可以改变自己的一件事，你会改变什么？",
        "你曾经撒过的最大的谎是什么？",
        "你小时候的梦想是什么？",
        "你最后悔的一件事是什么？",
        "你最害怕什么？",
        "你对自己的身体哪部分最满意？",
        "你做过最浪漫的事情是什么？",
        "你希望和喜欢的人一起做什么事？",
        "你最大的秘密是什么？",
        "你曾经为爱做过最傻的事情是什么？",
        "你相信一见钟情吗？",
        "你觉得性在感情中有多重要？",
        "你喜欢男生/女生什么样的特质？",
        "你会在约会软件上寻找真爱吗？",
        "你曾经对朋友有过超越友谊的感觉吗？",
        "你曾经幻想过和明星约会吗？是谁？",
        "你觉得自己最有魅力的地方是什么？",
        "你曾经被背叛过吗？",
        "你最近一次哭是因为什么？",
        "你最想和谁道歉？为什么？",
        "你最想实现的三个愿望是什么？",
        "你对自己的外貌最满意和最不满意的地方分别是什么？",
        "你希望自己十年后是什么样子？",
        "你做过最让自己骄傲的事情是什么？",
        "你最大的恐惧是什么？",
        "你曾经伤害过谁的感情？",
        "你相信灵魂伴侣吗？",
        "你对现在的生活满意吗？",
        "你如何看待年龄差距很大的恋情？",
        "你希望如何度过自己的婚礼？",
        "你如何看待异地恋？",
        "你曾经有过最尴尬的约会经历是什么？",
        "你如何看待同性婚姻？",
        "你觉得爱情和面包哪个更重要？",
        "你曾经为了钱做过违心的事吗？",
        "你觉得自己在感情中最需要改进的是什么？",
        "你做过的最奢侈的事情是什么？",
        "你如何看待前任？",
        "你曾经对朋友撒过谎吗？是什么？",
        "你做过的最不道德的事情是什么？",
        "你曾经有过嫉妒心最强烈的时刻是什么？",
        "你曾经有过最强烈的负罪感是因为什么？",
        "你希望如何度过自己的退休生活？",
        "你如何看待死亡？",
        "你曾经有过自杀的念头吗？",
        "你最大的成就是什么？",
        "你最想成为什么样的人？",
        "你觉得自己最像哪种动物？为什么？",
        "你曾经做过最危险的事情是什么？",
        "你希望有多少个孩子？",
        "你如何看待婚前协议？",
        "你曾经对父母撒过的最大的谎是什么？",
        "你最想感谢的人是谁？为什么？",
        "你曾经有过的最奇怪的梦想是什么？",
        "你如何看待网络恋情？",
        "你曾经在感情中受过最重的伤是什么？",
        "你希望自己死后被人们如何记住？",
        "你曾经有过的最糟糕的工作经历是什么？",
        "你如何看待金钱和幸福的关系？",
        "你曾经在公共场合做过最大胆的事情是什么？",
        "你曾经有过的最尴尬的身体反应是什么？",
        "你曾经在感情中做过的最自私的事情是什么？",
        "你曾经有过的最难以启齿的秘密是什么？",
        "你如何看待恋爱中的AA制？"
    )

    private val defaultDareQuestions = listOf(
        "和对方对视30秒，然后说出你的感受。",
        "给对方一个真诚的赞美，说出三个你欣赏的地方。",
        "模仿对方的某个小习惯或动作。",
        "分享你手机里最近拍的三张照片。",
        "唱一首情歌给在场的一位异性。",
        "对左边第三个人说‘我爱你’。",
        "分享你最近一次尴尬的经历。",
        "让在场一个人喂你吃一口食物。",
        "模仿一种动物，直到有人猜出来。",
        "亲吻在场一个人的脸颊。",
        "对某人耳语一句暧昧的话。",
        "表演一段性感舞蹈。",
        "说出你理想型的三个特征。",
        "和某人拥抱10秒钟。",
        "在社交媒体上发一条暧昧的状态，但不指名道姓。",
        "和对方玩掰手腕，输的人喝酒。",
        "分享你的手机屏幕一分钟。",
        "用外语说一句情话。",
        "轻咬一下某人的耳朵。",
        "对某人说‘你今晚很迷人’。",
        "说出你最喜欢的身体部位。",
        "和某人十指相扣30秒。",
        "对某人唱摇篮曲。",
        "模仿性感模特走猫步。",
        "喝一杯混合饮料（由其他人指定）。",
        "模仿在场一个人的走路姿势。",
        "说一个你的秘密癖好。",
        "对镜子抛媚眼一分钟。",
        "用嘴接住抛出的食物。",
        "表演一段台词，假装在表白。",
        "分享你手机里最尴尬的照片。",
        "和某人背对背挤气球。",
        "模仿名人签名。",
        "说出你昨晚梦到了谁。",
        "让某人给你画口红。",
        "表演触电的感觉。",
        "和某人玩对视游戏，谁先笑谁喝酒。",
        "说出你初吻的地点。",
        "对某人说‘如果我醉了，都是因为你’。",
        "表演如何诱惑一个人。",
        "分享你的恋爱史（几个前任）。",
        "模仿老师的上课方式。",
        "用肢体语言表达‘我爱你’。",
        "喝一口酒，然后对某人wink。",
        "表演一段街舞。",
        "说出你手机里最私密的照片是什么。",
        "和某人玩拇指大战。",
        "模仿婴儿哭。",
        "分享你最近购物车里的东西。",
        "对某人说‘你是我今晚的惊喜’。",
        "用嘴剥橘子皮。",
        "表演中彩票的反应。",
        "说出你最喜欢的亲吻方式。",
        "让某人给你按摩肩膀30秒。",
        "模仿狗叫。",
        "分享你最近搜索的关键词。",
        "和某人玩猜拳，输的人接受惩罚。",
        "模仿电影反派的笑声。",
        "说出你幻想过的约会场景。",
        "对某人唱生日快乐歌，即使今天不是他生日。",
        "表演晕倒。",
        "分享你的微信聊天记录（最新一条）。",
        "和某人交换一件衣物穿一分钟。",
        "模仿机器人跳舞。",
        "说出你做过最疯狂的事。",
        "喝一杯指定饮料。",
        "模仿老年人走路。",
        "分享你的屏保图片。",
        "对某人说‘你的眼睛很漂亮’。",
        "用嘴传递一张纸牌。",
        "表演功夫动作。",
        "说出你希望谁在场但不在。",
        "让某人公主抱你5秒。",
        "模仿外星人。",
        "分享你的第一个网名。",
        "和某人玩石头剪刀布，输的人被弹额头。",
        "模仿歌手唱歌跑调。",
        "说出你暗恋过的人。",
        "对某人说‘如果我赢了彩票，就带你环游世界’。",
        "表演肚子痛。",
        "分享你最近看的视频内容。",
        "和某人玩手指游戏。",
        "模仿猫叫。",
        "说出你身上最贵的东西。",
        "喝一口辣椒油（或替代品）。",
        "模仿猩猩捶胸。",
        "分享你的童年绰号。",
        "对某人说‘你让我心跳加速’。",
        "和某人玩瞪眼游戏，谁先眨眼谁输。",
        "分享你最近一次说谎的内容。",
        "对某人说‘你的笑容很治愈’。",
        "用嘴咬下某人手中的饼干。",
        "表演一段戏曲唱腔。",
        "说出你最近一次心动的原因。",
        "让某人背你走三步。",
        "模仿青蛙跳。",
        "分享你最喜欢的电影台词。",
        "对某人说‘你看起来很好吃’。"
    )

    /**
     * 获取真心话题目列表
     */
    fun getTruthQuestions(context: Context): MutableList<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TRUTH_QUESTIONS, null)

        return if (json == null) {
            // 第一次使用，保存默认题目
            saveTruthQuestions(context, defaultTruthQuestions.toMutableList())
            defaultTruthQuestions.toMutableList()
        } else {
            Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
        }
    }

    /**
     * 获取大冒险题目列表
     */
    fun getDareQuestions(context: Context): MutableList<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_DARE_QUESTIONS, null)

        return if (json == null) {
            // 第一次使用，保存默认题目
            saveDareQuestions(context, defaultDareQuestions.toMutableList())
            defaultDareQuestions.toMutableList()
        } else {
            Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
        }
    }

    /**
     * 保存真心话题目列表
     */
    fun saveTruthQuestions(context: Context, questions: MutableList<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(questions)
        prefs.edit { putString(KEY_TRUTH_QUESTIONS, json) }
    }

    /**
     * 保存大冒险题目列表
     */
    fun saveDareQuestions(context: Context, questions: MutableList<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(questions)
        prefs.edit { putString(KEY_DARE_QUESTIONS, json) }
    }

    /**
     * 添加真心话题目
     */
    fun addTruthQuestion(context: Context, question: String) {
        val questions = getTruthQuestions(context)
        questions.add(question)
        saveTruthQuestions(context, questions)
    }

    /**
     * 添加大冒险题目
     */
    fun addDareQuestion(context: Context, question: String) {
        val questions = getDareQuestions(context)
        questions.add(question)
        saveDareQuestions(context, questions)
    }

    /**
     * 删除真心话题目
     */
    fun deleteTruthQuestion(context: Context, question: String) {
        val questions = getTruthQuestions(context)
        questions.remove(question)
        saveTruthQuestions(context, questions)
    }

    /**
     * 删除大冒险题目
     */
    fun deleteDareQuestion(context: Context, question: String) {
        val questions = getDareQuestions(context)
        questions.remove(question)
        saveDareQuestions(context, questions)
    }

    /**
     * 重置题目库到默认状态
     */
    fun resetQuestions(context: Context) {
        saveTruthQuestions(context, defaultTruthQuestions.toMutableList())
        saveDareQuestions(context, defaultDareQuestions.toMutableList())
    }
}