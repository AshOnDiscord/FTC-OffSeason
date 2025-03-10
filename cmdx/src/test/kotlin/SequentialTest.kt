import com.millburnx.cmdx.commandGroups.Sequential
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class SequentialTest {
    @Test
    fun `sequential runs commands in order`() {
        var result = ""
        val sequential =
            Sequential("sequential") {
                Command("a") {
                    delay(100)
                    result += "a"
                }
                Command("b") {
                    delay(50)
                    result += "b"
                }
                Command("c") {
                    delay(75)
                    result += "c"
                }
            }

        runBlocking {
            sequential.run(this)
        }

        assertEquals("abc", result)
    }

    @Test
    fun `sequential cancels commands`() =
        runBlocking {
            var result = ""
            val sequential =
                Sequential("sequential.cancel") {
                    Command("a") {
                        delay(100)
                        result += "a"
                    }
                    Command("b") {
                        delay(50)
                        result += "b"
                    }
                    Command("c") {
                        delay(75)
                        result += "c"
                    }
                }

            val job =
                launch {
                    sequential.run(this)
                }

            delay(25)

            sequential.cancel()
            job.join()

            assertEquals("", result)
        }

    @Test
    fun `sequential cancels commands when scope is cancelled`() =
        runBlocking {
            var result = ""
            val sequential =
                Sequential("scope.cancel") {
                    Command("a") {
                        delay(100)
                        result += "a"
                    }
                    Command("b") {
                        delay(50)
                        result += "b"
                    }
                    Command("c") {
                        delay(75)
                        result += "c"
                    }
                }
            val job =
                launch {
                    sequential.run(this)
                }
            delay(25)
            job.cancel()
            job.join()
            assertEquals("", result)
        }

    @Test
    fun `Handle nested sequential commands`() {
        var result = ""
        val sequential =
            Sequential("nested") {
                Command("a") {
                    delay(100)
                    result += "a"
                }
                +Sequential("nested child") {
                    Command("b") {
                        delay(75)
                        result += "b"
                    }
                    Command("c") {
                        delay(25)
                        result += "c"
                    }
                }
                Command("d") {
                    delay(50)
                    result += "d"
                }
            }

        runBlocking {
            sequential.run(this)
        }

        assertEquals("abcd", result)
    }
}
