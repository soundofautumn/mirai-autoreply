package per.autumn.mirai.autoreply

import kotlinx.serialization.Serializable

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:48
 */
@Serializable
class Keyword {

    val raw: String
    private val regex: Regex by lazy { getKeywordRegex() }

    constructor(raw: String) {
        this.raw = raw
    }

    fun isMatchWith(text: String): Boolean {
        return regex.matches(text)
    }

    fun getAllMatched(text: String): List<String> {
        return regex.findAll(text).map { it.value }.toList()
    }

    private fun getKeywordRegex(): Regex {
        val split = Parser.split(this.raw)
        if (split.size == 1) {
            return Regex(split[0])
        }
        val result = StringBuilder()
        for (part in split) {
            if (Parser.isValidExpression(part)) {
                result.append("(.+)")
            } else {
                result.append(part)
            }
        }
        return Regex(result.toString())
    }

}