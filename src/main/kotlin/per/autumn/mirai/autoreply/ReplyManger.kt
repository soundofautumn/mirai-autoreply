package per.autumn.mirai.autoreply

import java.util.concurrent.ConcurrentHashMap

/**
 * @author SoundOfAutumn
 * @date 2023/5/21 22:00
 */
object ReplyManger {
    private val replyMap = Config.replyMap

    @Transient
    private val cacheMap = ConcurrentHashMap<Keyword, Response>()

    fun addReply(keyword: String, response: String) {
        replyMap[keyword] = response
    }

    fun removeReply(keyword: String) {
        val it = cacheMap.iterator()
        while (it.hasNext()) {
            if (it.next().key.raw == keyword) {
                it.remove()
                break
            }
        }
        replyMap.remove(keyword)
    }

    fun findReply(text: String): Reply? {
        cacheMap.forEach { (k, v) ->
            if (k.isMatchWith(text)) return Reply(k, v, text)
        }
        replyMap.forEach { (k, v) ->
            val keyword = Keyword(k)
            val response = Response(v)
            if (keyword.isMatchWith(text)) {
                cacheMap[keyword] = response
                return Reply(keyword, response, text)
            }
        }
        return null
    }
}