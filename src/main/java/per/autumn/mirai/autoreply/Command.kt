package per.autumn.mirai.autoreply

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.MessageChainBuilder
import per.autumn.mirai.autoreply.Config.enabledGroups
import per.autumn.mirai.autoreply.Config.replyMap
import per.autumn.mirai.autoreply.ReplyManager.removeByKeyword

/**
 * @author SoundOfAutumn
 * @date 2022/4/27 10:58
 */
class Command {
    object Add : SimpleCommand(AutoReply, "添加自动回复") {
        @Handler
        suspend fun handle(sender: CommandSender, keyword: String, response: String) {
            replyMap[keyword] = response
            sender.sendMessage("添加成功")
        }
    }

    object Remove : SimpleCommand(AutoReply, "移除自动回复") {
        @Handler
        suspend fun handle(sender: CommandSender, key: String) {
            removeByKeyword(key)
            sender.sendMessage("移除成功")
        }
    }

    object QueryReply : SimpleCommand(AutoReply, "查询所有自动回复") {
        @Handler
        suspend fun handle(sender: CommandSender) {
            replyMap.forEach {
                sender.sendMessage("${it.key} ${it.value}")
            }
        }
    }

    object EnableGroup : SimpleCommand(AutoReply, "添加群") {
        @Handler
        suspend fun handle(sender: CommandSender, groupId: Long) {
            enabledGroups.add(groupId)
            sender.sendMessage("添加成功")
        }
    }

    object DisableGroup : SimpleCommand(AutoReply, "移除群") {
        @Handler
        suspend fun handle(sender: CommandSender, groupId: Long) {
            enabledGroups.remove(groupId)
            sender.sendMessage("移除成功")
        }
    }

    object QueryEnabledGroups : SimpleCommand(AutoReply, "查询所有开启了自动回复的群") {
        @Handler
        suspend fun handle(sender: CommandSender) {
            val chainBuilder = MessageChainBuilder()
            for (id in enabledGroups) {
                chainBuilder.add(id.toString())
            }
            sender.sendMessage(chainBuilder.asMessageChain())
        }
    }

    object EnablePrivateChat : SimpleCommand(AutoReply, "允许私聊") {
        @Handler
        suspend fun handle(sender: CommandSender) {
            Config.enablePrivateChat = true
            sender.sendMessage("开启成功")
        }
    }
    object DisablePrivateChat : SimpleCommand(AutoReply, "禁止私聊") {
        @Handler
        suspend fun handle(sender: CommandSender) {
            Config.enablePrivateChat = false
            sender.sendMessage("禁止成功")
        }
    }
}
