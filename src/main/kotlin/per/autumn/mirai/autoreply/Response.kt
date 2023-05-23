package per.autumn.mirai.autoreply

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.objecthunter.exp4j.ExpressionBuilder
import java.io.File

/**
 * @author SoundOfAutumn
 * @date 2022/5/7 19:52
 */
class Response(raw: String) {

    private enum class Type(
        val isMatchWith: (String) -> Boolean,
        val buildWith: (MessageChainBuilder, String, MessageEvent, () -> String) -> Unit,
    ) {
        // always false to ensure that other types can be reached
        NORMAL({ false }, { mcb, raw, _, _ -> mcb.add(raw) }),
        AT_SENDER({ it == "@s" }, { mcb, _, event, _ ->
            if (event is GroupMessageEvent)
                mcb.add(At(event.sender))
            else
                AutoReply.logger.warning("At sender is only available in group chat. Skipping")
        }),
        AT_ALL({ it == "@a" }, { mcb, _, event, _ ->
            if (event is GroupMessageEvent)
                mcb.add(AtAll)
            else
                AutoReply.logger.warning("At all is only available in group chat. Skipping")
        }),
        QUOTE_SENDER({ it == "@q" }, { mcb, _, event, _ -> mcb.add(QuoteReply(event.message)) }),
        CURRENT_TIME({ it == "now" }, { mcb, _, _, _ -> mcb.add(nowTime()) }),
        CURRENT_DATE({ it == "today" }, { mcb, _, _, _ -> mcb.add(today()) }),
        CURRENT_DAY_OF_WEEK({ it == "day" }, { mcb, _, _, _ -> mcb.add(currentDayOfWeek()) }),
        PICTURE({ it.startsWith("pic") }, { mcb, raw, event, _ ->
            val name = raw.substring(4) // "pic".length + 1
            AutoReplyConfig.imageMap[name]?.let {
                File(AutoReply.imgFolder, it).toExternalResource().use { image ->
                    runBlocking {
                        mcb.add(event.sender.uploadImage(image))
                    }
                }
            } ?: let {
                AutoReply.logger.warning("Image $name not found. Skipping.")
            }
        }),
        CALCULATE_EXPRESSION({ it == "calc" }, { mcb, raw, _, consumer ->
            val expression = ExpressionBuilder(consumer()).build()
            if (expression.validate().isValid) {
                mcb.add(expression.evaluate().toString())
            } else {
                AutoReply.logger.warning("Invalid math expression: $raw, skipping.")
            }
        }),
    }

    private val parsed: List<Pair<Type, String>>

    init {
        val result: MutableList<Pair<Type, String>> = arrayListOf()
        Parser.split(raw).forEach { part ->
            if (Parser.isValidExpression(part)) {
                Parser.getContent(part).let { content ->
                    Type.values().find { it.isMatchWith(part) }?.let { type ->
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

    fun buildMessage(event: MessageEvent, keywordExpressions: List<String>): Message {
        val mcb = MessageChainBuilder()
        val iterator = keywordExpressions.iterator()
        parsed.forEach {
            it.first.buildWith(mcb, it.second, event) {
                if (iterator.hasNext()) {
                    return@buildWith iterator.next()
                } else {
                    AutoReply.logger.warning("Not enough keyword expressions. Skipping.")
                    return@buildWith ""
                }
            }
        }
        if (iterator.hasNext()) {
            AutoReply.logger.warning("Too many keyword expressions. Ignoring.")
        }
        return mcb.build()
    }
}