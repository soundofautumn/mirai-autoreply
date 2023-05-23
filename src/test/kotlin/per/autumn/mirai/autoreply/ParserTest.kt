package per.autumn.mirai.autoreply

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * @author SoundOfAutumn
 * @date 2023/5/21 14:04
 */
internal class ParserTest {

    @Test
    fun `hello ${1 + 2} world`() {
        val text = "hello \${1 + 2} world"
        val result = Parser.split(text)
        assertEquals(3, result.size)
        assertEquals("hello ", result[0])
        assertEquals("\${1 + 2}", result[1])
        assertEquals(" world", result[2])
    }

    @Test
    fun `hello ${1 + 2}`() {
        val text = "hello \${1 + 2}"
        val result = Parser.split(text)
        assertEquals(2, result.size)
        assertEquals("hello ", result[0])
        assertEquals("\${1 + 2}", result[1])
    }

    @Test
    fun `hello ${1 + 2}!`() {
        val text = "hello \${1 + 2}!"
        val result = Parser.split(text)
        assertEquals(3, result.size)
        assertEquals("hello ", result[0])
        assertEquals("\${1 + 2}", result[1])
        assertEquals("!", result[2])
    }

    @Test
    fun `${1 + 2}`() {
        val text = "\${1 + 2}"
        val result = Parser.split(text)
        assertEquals(1, result.size)
        assertEquals("\${1 + 2}", result[0])
    }

    @Test
    fun `${1 + 2} world`() {
        val text = "\${1 + 2} world"
        val result = Parser.split(text)
        assertEquals(2, result.size)
        assertEquals("\${1 + 2}", result[0])
        assertEquals(" world", result[1])
    }

    @Test
    fun `${1 + 2`() {
        val text = "\${1 + 2"
        val result = Parser.split(text)
        assertEquals(1, result.size)
        assertEquals("\${1 + 2", result[0])
    }

    @Test
    fun `hello world`() {
        val text = "hello world"
        val result = Parser.split(text)
        assertEquals(1, result.size)
        assertEquals("hello world", result[0])
    }

    @Test
    fun `${1 + 2 ${1 + 2}`() {
        val text = "\${1 + 2 \${1 + 2}"
        val result = Parser.split(text)
        assertEquals(2, result.size)
        assertEquals("\${1 + 2 ", result[0])
        assertEquals("\${1 + 2}", result[1])
    }

    @Test
    fun `${1 + 2 ${{1 + 2}`() {
        val text = "\${1 + 2 \${{1 + 2}"
        val result = Parser.split(text)
        assertEquals(1, result.size)
        assertEquals("\${1 + 2 \${{1 + 2}", result[0])
    }

    @Test
    fun isValidExpressionTest() {
        assertTrue(Parser.isValidExpression("\${}"))
        assertTrue(Parser.isValidExpression("\${123}"))
        assertFalse(Parser.isValidExpression("\${123"))
        assertFalse(Parser.isValidExpression("\${"))
        assertFalse(Parser.isValidExpression("\${}}"))
        assertFalse(Parser.isValidExpression("\${{}"))
    }
}