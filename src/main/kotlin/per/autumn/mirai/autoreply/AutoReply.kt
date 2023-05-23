package per.autumn.mirai.autoreply

import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.UserMessageEvent
import per.autumn.mirai.autoreply.AutoReplyConfig.enabledGroups
import java.io.File

object AutoReply : KotlinPlugin(
    JvmPluginDescriptionBuilder("per.autumn.mirai.autoreply.plugin", "2.0.1")
        .info("一个简易的自动回复插件")
        .name("auto-reply").build()
) {
    val imgFolder = File(dataFolder, "/img")
    override fun onEnable() {
        AutoReplyConfig.reload()
        registerCommands()
        //添加群组监听
        GlobalEventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { enabledGroups.contains(it.group.id) }
            .subscribeAlways<GroupMessageEvent> {
                ReplyManger.findReply(it.message.contentToString())?.let { reply ->
                    this.subject.sendMessage(reply.getResponseMsg(this))
                }
            }
        //添加私聊监听
        GlobalEventChannel
            .subscribeAlways<UserMessageEvent> {
                if (AutoReplyConfig.enablePrivateChat) {
                    ReplyManger.findReply(it.message.contentToString())?.let { reply ->
                        this.subject.sendMessage(reply.getResponseMsg(this))
                    }
                }
            }
    }

    override fun onDisable() {
        unregisterCommands()
        AutoReplyConfig.save()
    }

    private val commands by lazy {
        listOf<Command>(
            AutoReplyCommands.AddAutoReply,
            AutoReplyCommands.RemoveAutoReply,
            AutoReplyCommands.QueryReply,
            AutoReplyCommands.EnableGroup,
            AutoReplyCommands.DisableGroup,
            AutoReplyCommands.AddImage,
            AutoReplyCommands.RemoveImage,
            AutoReplyCommands.ClearImage,
            AutoReplyCommands.QueryEnabledGroups,
            AutoReplyCommands.EnablePrivateChat,
            AutoReplyCommands.DisablePrivateChat,
        )
    }

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