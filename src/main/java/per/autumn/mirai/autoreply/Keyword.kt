package per.autumn.mirai.autoreply

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:48
 */
data class Keyword(private val pattern: String) {

    fun isMatchWith(text: String): Boolean {
        if (!pattern.contains("$")) {
            return pattern == text
        }
        return text.contains(pattern.replace(Regex("\\$\\{.*}"), ""))
    }

    fun parse(text: String, patternType: String): String {
        var res = text
        pattern.split(patternType).forEach { res = res.replace(it, "") }
        return res
    }
}