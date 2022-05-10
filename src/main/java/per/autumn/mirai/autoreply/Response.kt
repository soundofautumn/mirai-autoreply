package per.autumn.mirai.autoreply

import cn.hutool.core.date.DateUtil
import cn.hutool.core.math.Calculator
import net.mamoe.mirai.message.data.MessageChain
import net.objecthunter.exp4j.ExpressionBuilder

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:52
 */
data class Response(val pattern: String) {
    companion object {
        const val EmptyString = ""
        const val Quote = "@q"
        const val AtAll = "@a"
        const val Now = "\${now}"
        const val Today = "\${today}"
        const val Calc = "\${calc}"
    }

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
                res = res.replace(Now, DateUtil.now())
            }
            if (res.contains(Today)) {
                res = res.replace(Today, DateUtil.today())
            }
            if (res.contains(Calc)) {
                res = res.replace(Calc, ExpressionBuilder(keyword.parse(text, Calc)).build().evaluate().toString())
                //res = res.replace(Calc, Calculator.conversion(keyword.parse(text, Calc)).toString())
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