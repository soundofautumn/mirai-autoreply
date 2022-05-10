package per.autumn.mirai.autoreply

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.UserMessageEvent
import per.autumn.mirai.autoreply.Config.enabledGroups
import per.autumn.mirai.autoreply.ReplyManager.getResponse
import per.autumn.mirai.autoreply.ReplyManager.hasKeyword

object AutoReply : JavaPlugin(
    JvmPluginDescriptionBuilder("per.autumn.mirai.autoreply.plugin", "1.3")
        .info("一个简易的自动回复插件")
        .name("auto-reply").build()
) {
    override fun onEnable() {
        Config.reload()
        registerCommands()
        GlobalEventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { enabledGroups.contains(it.group.id) }
            .subscribeAlways<GroupMessageEvent> {
                if (hasKeyword(it.message)) {
                    it.group.sendMessage(getResponse(it))
                }
            }
        GlobalEventChannel
            .subscribeAlways<UserMessageEvent> {
                if (Config.enablePrivateChat && hasKeyword(it.message)) {
                    it.subject.sendMessage(getResponse(it))
                }
            }

    }

    override fun onDisable() {
        Config.save()
    }

    private fun registerCommands() {
        registerCommands(
            Command.Add,
            Command.Remove,
            Command.QueryReply,
            Command.EnableGroup,
            Command.DisableGroup,
            Command.QueryEnabledGroups,
            Command.EnablePrivateChat,
            Command.DisablePrivateChat
        )
    }

    private fun registerCommands(vararg commands: net.mamoe.mirai.console.command.Command) {
        commands.forEach {
            CommandManager.registerCommand(it, true)
        }
    }
}