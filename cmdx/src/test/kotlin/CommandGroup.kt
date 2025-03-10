
import com.millburnx.cmdx.commandGroups.Parallel
import com.millburnx.cmdx.commandGroups.Sequential
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandGroup {
    @Test
    fun `Sequential and Parallel can be internested`() {
        var result = ""
        val sequence =
            Sequential("sequence") {
                +Parallel("parallel") {
                    Command("c") {
                        delay(100)
                        result += "c"
                    }
                    Command("a") {
                        result += "a"
                    }
                    +Sequential("sequence2") {
                        Command("b") {
                            delay(25)
                            result += "b"
                        }
                        Command("d") {
                            delay(125)
                            result += "d"
                        }
                    }
                }
                Command("e") {
                    delay(50)
                    result += "e"
                }
                +Sequential("sequence2") {
                    Command("f") {
                        delay(75)
                        result += "f"
                    }
                    Command("g") {
                        result += "g"
                    }
                }
            }

        runBlocking {
            sequence.run(this)
        }

        assertEquals("abcdefg", result)
    }
}
