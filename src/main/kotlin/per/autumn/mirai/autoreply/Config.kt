package per.autumn.mirai.autoreply

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

/**
 * @author SoundOfAutumn
 * @date 2022/4/27 11:00
 */
object Config : AutoSavePluginConfig("config") {
    val replyMap: MutableMap<String, String> by value()
    val enabledGroups: MutableList<Long> by value()
    val imageMap: MutableMap<String, String> by value()
    var enablePrivateChat: Boolean by value(false)
}