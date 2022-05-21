package per.autumn.mirai.autoreply

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.ClassUtil
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
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
    val week = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    return week[DateUtil.thisDayOfWeek() - 1]
}

fun calculateExpression(expression: String): String {
//    return Calculator.conversion(expression)
    return ExpressionBuilder(expression).build().evaluate().toString()
}

/**
 * 必须在MiraiConsole启动后才能被调用
 */
fun getAllCommands(): List<Command> {
    val res = mutableListOf<Command>()
    val clazz = ClassUtil.scanPackage("per.autumn.mirai.autoreply") { Command::class.java.isAssignableFrom(it) }
    clazz.forEach {
        res.add(it.getField("INSTANCE").get(null) as Command)
    }
    return res
}

fun Group.findMemberOrNull(group: Group, name: String): Member? {
    group.members.forEach {
        if (it.nick.lowercase() == name.lowercase()) {
            return it
        }
    }
    return fuzzyFindMemberOrNull(group, name)
}

private fun fuzzyFindMemberOrNull(group: Group, name: String): Member? {
    //双重筛选
    //第一类是匹配率大于0.2
    //第二类是匹配率大于0.8
    val candidates: MutableSet<Member> = HashSet()
    val highPossibleCandidates: MutableSet<Member> = HashSet()
    group.members.forEach {
        val matchRate: Float = getMatchRate(name.lowercase(), it.nick.lowercase())
        if (matchRate > 0.2) {
            candidates.add(it)
            if (matchRate > 0.8) {
                highPossibleCandidates.add(it)
            }
        }
    }
    return if (candidates.size == 1) {
        candidates.first()
    } else if (highPossibleCandidates.size == 1) {
        highPossibleCandidates.first()
    } else {
        null
    }
}

private fun getMatchRate(origin: String, target: String): Float {
    if (origin == target) {
        return 1.0f
    }
    val originBytes = origin.toByteArray()
    val targetBytes = target.toByteArray()
    var originIndex = 0
    var targetIndex = 0
    //当target的下标未移动到尽头时
    while (targetIndex != targetBytes.size - 1) {
        //以origin当前的字符遍历target
        for (i in originIndex until originBytes.size) {
            //如果有匹配
            if (originBytes[originIndex] == targetBytes[targetIndex]) {
                //origin步进一格
                ++originIndex
                break
            }
        }
        //target步进一格
        ++targetIndex
    }
    //返回origin匹配上的百分比
    return originIndex.toFloat() / originBytes.size
}

