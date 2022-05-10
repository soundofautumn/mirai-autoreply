package per.autumn.mirai.autoreply

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import per.autumn.mirai.autoreply.Config.replyMap

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 22:26
 */
object ReplyManager {
    fun hasKeyword(msg: MessageChain): Boolean {
        return hasKeyword(msg.contentToString())
    }

    private fun hasKeyword(s: String): Boolean {
        for (keyword in replyMap.keys) {
            if (Keyword(keyword).isMatchWith(s)) {
                return true
            }
        }
        return false
    }

    private fun getKeyword(msg: MessageChain): String? {
        return getKeyword(msg.contentToString())
    }

    private fun getKeyword(s: String): String? {
        for (keyword in replyMap.keys) {
            if (Keyword(keyword).isMatchWith(s)) {
                return keyword
            }
        }
        return null
    }

    fun removeByKeyword(text: String) {
        replyMap.remove(text)
    }

    fun getResponse(event: MessageEvent): Message {
        val msg = event.message
        val keyword = getKeyword(msg)
        val chainBuilder = MessageChainBuilder()
        val response = Response(replyMap[keyword]!!)
        addExtraMessage(event, response, chainBuilder)
        chainBuilder.add(response.parse(keyword!!, msg))
        return chainBuilder.asMessageChain()
    }

    private fun addExtraMessage(event: MessageEvent, response: Response, cb: MessageChainBuilder) {
        if (response.quote) {
            cb.add(QuoteReply(event.message))
        }
        if (response.atAll) {
            cb.add(AtAll)
        }
    }
}