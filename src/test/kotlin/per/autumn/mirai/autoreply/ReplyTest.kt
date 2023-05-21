package per.autumn.mirai.autoreply

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author SoundOfAutumn
 * @date 2023/5/21 20:47
 */
internal class ReplyTest {

    @Test
    fun normalStringTest() {
        val keyword = Keyword("test")
        val response = Response("test")
        val reply = Reply(keyword, response, "test")
        assertTrue(reply.isKeywordMatch())
    }
}