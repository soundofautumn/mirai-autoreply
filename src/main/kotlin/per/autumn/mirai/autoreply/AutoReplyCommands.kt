package per.autumn.mirai.autoreply

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.http.HttpUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChainBuilder
import per.autumn.mirai.autoreply.Config.enabledGroups
import per.autumn.mirai.autoreply.Config.replyMap
import per.autumn.mirai.autoreply.ReplyManager.removeByKeyword
import java.io.File

/**
 * @author SoundOfAutumn
 * @date 2022/4/27 10:58
 */
@Suppress("UNUSED")
class AutoReplyCommands {
    object AddAutoReply : SimpleCommand(AutoReply, "添加自动回复") {
        @Handler
        suspend fun handle(sender: CommandSender, vararg args: String) {
            val keyword = args[0]
            var response = args[1]
            if (args.size > 2) {
                for (s in (2 until args.size)) {
                    response += args[s]
                }
            }
            replyMap[keyword] = response
            sender.sendMessage("添加成功")
        }
    }

    object RemoveAutoReply : SimpleCommand(AutoReply, "移除自动回复") {
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

    object AddImage : SimpleCommand(AutoReply, "添加图片") {
        @Handler
        suspend fun handle(sender: CommandSender, name: String, image: Image) {
            val uuid = IdUtil.randomUUID()
            HttpUtil.downloadFile(image.queryUrl(), FileUtil.file(AutoReply.imgFolder, "$uuid.jpg"))
            Config.imageMap[name] = uuid
        }
    }

    object RemoveImage : SimpleCommand(AutoReply, "移除图片") {
        @Handler
        suspend fun handle(sender: CommandSender, name: String) {
            val imageName = Config.imageMap.remove(name)
            if (imageName == null) {
                sender.sendMessage("该图片不存在")
            }
            FileUtil.del(File(AutoReply.imgFolder, "$imageName.jpg"))
        }
    }

    object ClearImage : SimpleCommand(AutoReply, "清空图片") {
        @Handler
        fun handle(sender: CommandSender) {
            Config.imageMap.clear()
            FileUtil.clean(AutoReply.imgFolder)
        }
    }

    object QueryEnabledGroups : SimpleCommand(AutoReply, "查询所有群") {
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
