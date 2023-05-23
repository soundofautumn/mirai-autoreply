package per.autumn.mirai.autoreply

/**
 * @author SoundOfAutumn
 * @date 2023/5/21 13:43
 */
object Parser {

    fun split(text: String): List<String> {
        // 不需要分割
        if (!text.contains("$")) {
            return listOf(text)
        }

        var slow = 0
        var fast = 0
        val result = mutableListOf<String>()
        while (slow != text.length) {
            if (fast == text.length) {
                result.add(text.substring(slow, fast))
                break
            }
            if (text[fast] != '$') {
                fast++
            } else {
                val end = findEnd(fast, text)
                if (end == -1) {
                    fast++
                    continue
                }
                if (slow != fast)
                    result.add(text.substring(slow, fast))
                result.add(text.substring(fast, end + 1))
                if (end == text.lastIndex) {
                    break
                }
                slow = end + 1
                fast = end + 1
            }
        }
        return result
    }

    private fun findEnd(begin: Int, text: String): Int {
        assert(text[begin] == '$')
        var i = begin
        if (text[++i] != '{') {
            return -1
        }
        // 此时 text[i] = '{'
        i++
        while (i != text.length) {
            // 不能含有其他的 '$' 和 '{'
            if (text[i] == '$' || text[i] == '{') {
                return -1
            }
            if (text[i] == '}') {
                return i
            }
            i++
        }
        return -1
    }

    fun isValidExpression(text: String): Boolean {
        if (text.length < 3) return false
        return text[0] == '$' && findEnd(0, text) == text.lastIndex
    }

    fun getContent(text: String): String {
        assert(isValidExpression(text))
        return text.substring(2, text.lastIndex)
    }

}
