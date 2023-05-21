package per.autumn.mirai.autoreply

import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:52
 */
@Serializable
class Response {

    private enum class Type(val pattern: String, val buildWith: (MessageChainBuilder, String, MessageEvent) -> Unit) {
        NORMAL("", { mcb, raw, _ -> mcb.add(raw) }),
        AT_SENDER("@s", { mcb, _, event -> mcb.add(At(event.sender)) }),
        AT_ALL("@a", { mcb, _, _ -> mcb.add(AtAll) }),
        QUOTE_SENDER("@q", { mcb, _, event -> mcb.add(QuoteReply(event.message)) }),
        CURRENT_TIME("now", { mcb, _, _ -> mcb.add(nowTime()) }),
        CURRENT_DATE("today", { mcb, _, _ -> mcb.add(today()) }),
        CURRENT_DAY_OF_WEEK("day", { mcb, _, _ -> mcb.add(currentDayOfWeek()) }),
//        CALCULATE_EXPRESSION("calc", { mcb, raw, _ -> mcb.add(calculateExpression(raw)) }),
    }

    private val parsed: List<Pair<Type, String>>

    constructor(raw: String) {
        val split = Parser.split(raw)
        val result: MutableList<Pair<Type, String>> = arrayListOf()
        split.forEach { part ->
            if (Parser.isValidExpression(part)) {
                Parser.getContent(part).let { content ->
                    Type.values().find { type -> type.pattern == content }?.let { type ->
                        result.add(Pair(type, content))
                    } ?: let {
                        AutoReply.logger.warning("Invalid expression: $part, handling as normal string.")
                        result.add(Pair(Type.NORMAL, part))
                    }
                }
            } else {
                result.add(Pair(Type.NORMAL, part))
            }
        }
        parsed = result
    }

    fun buildMessage(event: MessageEvent): Message {
        val mcb = MessageChainBuilder()
        for (pair in parsed) {
            pair.first.buildWith(mcb, pair.second, event)
        }
        return mcb.build()
    }
}