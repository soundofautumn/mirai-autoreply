package per.autumn.mirai.autoreply

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * @author SoundOfAutumn
 * @date 2023/5/21 17:50
 */
internal class KeywordTest {

    @Test
    fun matchTest() {
        assertTrue(Keyword("你好").isMatchWith("你好"))
        assertTrue(Keyword("计算\${calc}").isMatchWith("计算1 + 2"))
        assertFalse(Keyword("计算\${calc}。").isMatchWith("计算sin(1)"))
        assertTrue(Keyword("计算\${calc}。").isMatchWith("计算sin(1)。"))
    }
}