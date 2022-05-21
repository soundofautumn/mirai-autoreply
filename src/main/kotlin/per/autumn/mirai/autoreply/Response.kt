package per.autumn.mirai.autoreply

import net.mamoe.mirai.message.data.MessageChain

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:52
 */
data class Response(val pattern: String) {
    companion object {
        const val EmptyString = ""
        const val Quote = "@q"
        const val AtAll = "@a"
        const val AtSender = "@s"
        const val Now = "\${now}"
        const val Today = "\${today}"
        const val DayOfWeek = "\${day}"
        const val Calc = "\${calc}"
    }

    val atSender = pattern.contains(AtSender)
    val quote = pattern.contains(Quote)
    val atAll = pattern.contains(AtAll)


    fun parse(keyword: String, msg: MessageChain): String {
        return parse(Keyword(keyword), msg.contentToString())
    }

    private fun parse(keyword: Keyword, text: String): String {
        return if (!pattern.contains("$")) {
            removeExtra(pattern)
        } else {
            var res = pattern
            if (res.contains(Now)) {
                res = res.replace(Now, nowTime())
            }
            if (res.contains(Today)) {
                res = res.replace(Today, today())
            }
            if (res.contains(DayOfWeek)) {
                res = res.replace(DayOfWeek, currentDayOfWeek())
            }
            if (res.contains(Calc)) {
                res = res.replace(Calc, calculateExpression(keyword.parse(text, Calc)))
            }
            removeExtra(res)
        }
    }

    private fun removeExtra(text: String): String {
        var res = text
        if (quote) {
            res = res.replace(Quote, EmptyString)
        }
        if (atAll) {
            res = res.replace(AtAll, EmptyString)
        }
        return res
    }
}