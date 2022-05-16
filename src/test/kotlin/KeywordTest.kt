package per.autumn.mirai.autoreply

/**
 * @author SoundOfAutumn
 * @date 2022/5/16 10:21
 */
fun main() {
    println(Keyword("""123${123}456""").isMatchWith("""12123456"""))
}