package per.autumn.mirai.autoreply

import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File
import net.objecthunter.exp4j.ExpressionBuilder

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:52
 */
@Serializable
class Response {

    private enum class Type(
        val isMatchWith: (String) -> Boolean,
        val buildWith: suspend (MessageChainBuilder, String, MessageEvent, () -> String) -> Unit,
    ) {
        NORMAL({ true }, { mcb, raw, _, _ -> mcb.add(raw) }),
        AT_SENDER({ part -> part == "@s" }, { mcb, _, event, _ ->
            if (event is GroupMessageEvent)
                mcb.add(At(event.sender))
            else
                AutoReply.logger.warning("At sender is only available in group chat. Skipping")
        }),
        AT_ALL({ part -> part == "@a" }, { mcb, _, event, _ ->
            if (event is GroupMessageEvent)
                mcb.add(AtAll)
            else
                AutoReply.logger.warning("At all is only available in group chat. Skipping")
        }),
        QUOTE_SENDER({ part -> part == "@q" }, { mcb, _, event, _ -> mcb.add(QuoteReply(event.message)) }),
        CURRENT_TIME({ part -> part == "now" }, { mcb, _, _, _ -> mcb.add(nowTime()) }),
        CURRENT_DATE({ part -> part == "today" }, { mcb, _, _, _ -> mcb.add(today()) }),
        CURRENT_DAY_OF_WEEK({ part -> part == "day" }, { mcb, _, _, _ -> mcb.add(currentDayOfWeek()) }),
        PICTURE({ part -> part.startsWith("pic") }, { mcb, raw, event, _ ->
            val name = raw.substring(4) // "pic".length + 1
            Config.imageMap[name]?.let {
                File(AutoReply.imgFolder, it).toExternalResource().use { image ->
                    mcb.add(event.sender.uploadImage(image))
                }
            } ?: let {
                AutoReply.logger.warning("Image $name not found. Skipping.")
            }
        }),
        CALCULATE_EXPRESSION({ part -> part == "calc" }, { mcb, raw, _, consumer ->
            val expression = ExpressionBuilder(consumer()).build()
            if (expression.validate().isValid) {
                mcb.add(expression.evaluate().toString())
            } else {
                AutoReply.logger.warning("Invalid expression: $raw, skipping.")
            }
        }),
    }

    private val parsed: List<Pair<Type, String>>

    constructor(raw: String) {
        val split = Parser.split(raw)
        val result: MutableList<Pair<Type, String>> = arrayListOf()
        split.forEach { part ->
            if (Parser.isValidExpression(part)) {
                Parser.getContent(part).let { content ->
                    Type.values().find { type -> type.isMatchWith(part) }?.let { type ->
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

    suspend fun buildMessage(event: MessageEvent, keywordExpressions: List<String>): Message {
        val mcb = MessageChainBuilder()
        val iterator = keywordExpressions.iterator()
        parsed.forEach { pair ->
            pair.first.buildWith(mcb, pair.second, event) {
                if (iterator.hasNext()) {
                    return@buildWith iterator.next()
                } else {
                    AutoReply.logger.warning("Not enough keyword expressions. Skipping.")
                    return@buildWith ""
                }
            }
        }
        return mcb.build()
    }
}