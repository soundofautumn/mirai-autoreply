package per.autumn.mirai.autoreply

import cn.hutool.core.date.DateUtil

/**
 * @author SoundOfAutumn
 * @date 2022/5/10 13:37
 */
fun getCurrentDayOfWeek(): String {
    val week = arrayOf("", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")
    return week[DateUtil.thisDayOfWeek()]
}