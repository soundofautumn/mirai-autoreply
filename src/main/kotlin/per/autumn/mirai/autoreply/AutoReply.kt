package per.autumn.mirai.autoreply

import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.UserMessageEvent
import per.autumn.mirai.autoreply.Config.enabledGroups
import per.autumn.mirai.autoreply.Config.replyMap
import java.io.File

object AutoReply : KotlinPlugin(
    JvmPluginDescriptionBuilder("per.autumn.mirai.autoreply.plugin", "1.3.2")
        .info("一个简易的自动回复插件")
        .name("auto-reply").build()
) {
    val imgFolder = File(dataFolder, "/img")
    override fun onEnable() {
        Config.reload()
        registerCommands()
        //添加群组监听
        GlobalEventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { enabledGroups.contains(it.group.id) }
            .subscribeAlways<GroupMessageEvent> {
                replyMap.forEach { (keyword, response) ->
                    Reply(keyword, response, it.message.contentToString()).let {
                        if (it.isKeywordMatch()) {
                            this.subject.sendMessage(it.getResponseMsg(this))
                        }
                    }
                }
            }
        //添加私聊监听
        GlobalEventChannel
            .subscribeAlways<UserMessageEvent> {
                if (Config.enablePrivateChat ) {
                    replyMap.forEach { (keyword, response) ->
                        Reply(keyword, response, it.message.contentToString()).let {
                            if (it.isKeywordMatch()) {
                                this.subject.sendMessage(it.getResponseMsg(this))
                            }
                        }
                    }
                }
            }
    }

    override fun onDisable() {
        unregisterCommands()
        Config.save()
    }

    private val commands = getAllCommands()

    private fun registerCommands(commands: List<Command> = AutoReply.commands) {
        commands.forEach {
            it.register()
        }
    }

    private fun unregisterCommands(commands: List<Command> = AutoReply.commands) {
        commands.forEach {
            it.unregister()
        }
    }
}