package per.autumn.mirai.autoreply

import org.junit.jupiter.api.Test

/**
 * @author SoundOfAutumn
 * @date 2022/5/16 10:21
 */
class KeywordTest {
    @Test
    fun `keyword match with test`() {
        println(Keyword("""123${123}456""").isMatchWith("""12123456"""))
    }
}