package per.autumn.mirai.autoreply

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * @author SoundOfAutumn
 * @date 2023/5/21 14:40
 */
internal class UtilsKtTest {

    @Test
    fun calculateExpressionTest() {
        assertEquals(calculateExpression("1 + 2"), "3.0")
        assertEquals(calculateExpression("sin(1)"), "0.8414709848078965")
    }

    @Test
    fun isValidCalculateExpressionTest() {
        assert(isValidCalculateExpression("1 + 2"))
        assertFalse(isValidCalculateExpression(" 1 +"))
    }
}