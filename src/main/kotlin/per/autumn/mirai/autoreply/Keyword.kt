package per.autumn.mirai.autoreply

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:48
 */
class Keyword(val raw: String) {

    private val regex: Regex by lazy { getKeywordRegex() }

    fun isMatchWith(text: String): Boolean {
        return regex.matches(text)
    }

    fun getAllMatched(text: String): List<String> {
        return regex.findAll(text).map { it.value }.toList()
    }

    private fun getKeywordRegex(): Regex {
        Parser.split(this.raw).let {
            if (it.size == 1) {
                return Regex(it[0])
            }
            StringBuilder().apply {
                it.forEach { part ->
                    if (Parser.isValidExpression(part)) {
                        append("(.+)")
                    } else {
                        append(part)
                    }
                }
            }.toString().let { regex ->
                return Regex(regex)
            }
        }
    }

}