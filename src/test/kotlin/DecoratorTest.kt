import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class DecoratorTest {
    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()
    private val originalOut = System.out
    private val originalErr = System.err

    @Before
    fun setUp() {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    @Test
    fun testWordifierWhenDelimiterIsWrong() {
        val component = Component(1234.toString())
        assertEquals("1234", Decorator.Wordify(component, delimiter = ",").decorate())
    }

    @Test
    fun testWordifierWhenLangIsWrong() {
        val component = Component(1234.toString())
        assertEquals(
            "one,two,three,four",
            Decorator.Wordify(component = component, lang = "fr").decorate()
        )
        assert(outContent.toString().startsWith("Loading english as the properties for fr is not found"))
    }

    @Test
    fun testWordifierWhenDecorDelimIsDifferent() {
        val component = Component(1234.toString())
        assertEquals("one-two-three-four", Decorator.Wordify(component, decorDelim = "-").decorate())
    }

    @Test
    fun testWordifierWhenStringContainsNonNumericValues() {
        var component = Component("")
        assertEquals("", Decorator.Wordify(component).decorate())

        component = Component("abcd")
        assertEquals("", Decorator.Wordify(component).decorate())
    }


    @Test
    fun testWordifierForNegativeValue() {
        val component = Component("-1")
        assertEquals("minus,one", Decorator.Wordify(component).decorate())
    }

    @Test
    fun testWordifier() {
        val component = Component(1234.toString())
        assertEquals("one,two,three,four", Decorator.Wordify(component).decorate())
    }


    @After
    fun tearDown() {
        System.setOut(originalOut)
        System.setErr(originalErr)
    }
}