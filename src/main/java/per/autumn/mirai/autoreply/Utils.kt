package per.autumn.mirai.autoreply

import cn.hutool.core.date.DateUtil
import net.objecthunter.exp4j.ExpressionBuilder

/**
 * @author SoundOfAutumn
 * @date 2022/5/10 13:37
 */
fun nowTime(): String {
    return DateUtil.now().split(" ")[1]
}

fun today(): String {
    return DateUtil.today()
}

fun currentDayOfWeek(): String {
    val week = arrayOf("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")
    return week[DateUtil.thisDayOfWeek() - 1]
}

fun calculateExpression(expression: String): String {
//    return Calculator.conversion(expression)
    return ExpressionBuilder(expression).build().evaluate().toString()
}