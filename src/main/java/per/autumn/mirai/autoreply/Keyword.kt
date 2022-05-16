package per.autumn.mirai.autoreply

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:48
 */
data class Keyword(private val pattern: String) {

    private val needParse = pattern.contains("$")
    private val parserRegex = if (needParse) pattern.replace(Regex("""\$\{.*}"""), """\$\{.*}""") else pattern

    fun isMatchWith(text: String): Boolean {
        return text.contains(Regex(parserRegex))
    }

    fun parse(text: String, patternType: String): String {
        var res = text
        pattern.split(patternType).forEach { res = res.replace(it, "") }
        return res
    }
}