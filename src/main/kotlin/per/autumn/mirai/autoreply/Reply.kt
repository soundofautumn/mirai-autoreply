package per.autumn.mirai.autoreply

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message

/**
 * @author SoundOfAutumn
 * @date 2023/5/21 18:08
 */
class Reply(private val keyword: Keyword, private val response: Response, private val text: String) {

    fun isKeywordMatch(): Boolean {
        return keyword.isMatchWith(text)
    }

    suspend fun getResponseMsg(event: MessageEvent): Message {
        return response.buildMessage(event, keyword.getAllMatched(text))
    }


}