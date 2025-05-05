import com.millburnx.cmdx.Command
import com.millburnx.cmdx.commandGroups.ParallelDeadline
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ParallelDeadlineTest {
    @Test
    fun `ParallelDeadline runs commands in parallel`() {
        var result = ""
        val parallelDeadline =
            ParallelDeadline(
                "parallel",
                Command("deadline") {
                    delay(200)
                },
            ) {
                Command("c") {
                    delay(100)
                    result += "c"
                }
                Command("a") {
                    delay(50)
                    result += "a"
                }
                Command("b") {
                    delay(75)
                    result += "b"
                }
            }
        runTest {
            parallelDeadline.run(this)
        }
        assertEquals("abc", result)
    }

    @Test
    fun `ParallelDeadline cancels commands after deadline`() {
        var result = ""
        val parallelDeadline =
            ParallelDeadline(
                "parallel",
                Command("deadline") {
                    delay(85)
                    println("Deadline cancelled")
                },
            ) {
                Command("c") {
                    delay(100)
                    result += "c"
                }
                Command("a") {
                    delay(50)
                    result += "a"
                }
                Command("b") {
                    delay(75)
                    result += "b"
                }
            }

        runTest {
            parallelDeadline.run(this)
        }
        assertEquals("ab", result)
    }

    @Test
    fun `ParalleDeadline cancels commads after deadline is cancelled`() =
        runTest {
            val deadline =
                Command("deadline") {
                    delay(200)
                    println("Deadline cancelled")
                }

            var result = ""
            val parallelDeadline =
                ParallelDeadline(
                    "parallel",
                    deadline,
                ) {
                    Command("c") {
                        delay(100)
                        result += "c"
                    }
                    Command("a") {
                        delay(50)
                        result += "a"
                    }
                    Command("b") {
                        delay(75)
                        result += "b"
                    }
                }

            val job =
                launch {
                    parallelDeadline.run(this)
                }
            delay(80)
            deadline.cancel()
            job.join()
            assertEquals("ab", result)
        }

    @Test
    fun `ParallelDeadline cancels commands`() =
        runTest {
            var result = ""
            val parallelDeadline =
                ParallelDeadline(
                    Command("deadline") {
                        delay(200)
                        println("Deadline cancelled")
                    },
                ) {
                    Command("c") {
                        delay(100)
                        result += "c"
                    }
                    Command("a") {
                        delay(50)
                        result += "a"
                    }
                    Command("b") {
                        delay(75)
                        result += "b"
                    }
                }

            val job =
                launch {
                    parallelDeadline.run(this)
                }
            delay(80)
            parallelDeadline.cancel()
            job.join()
            assertEquals("ab", result)
        }

    @Test
    fun `ParallelDeadline cancels commands when scope is cancelled`() =
        runTest {
            var result = ""
            val parallelDeadline =
                ParallelDeadline(
                    Command("deadline") {
                        delay(200)
                        println("Deadline cancelled")
                    },
                ) {
                    Command("c") {
                        delay(100)
                        result += "c"
                    }
                    Command("a") {
                        delay(50)
                        result += "a"
                    }
                    Command("b") {
                        delay(75)
                        result += "b"
                    }
                }

            val job =
                launch {
                    parallelDeadline.run(this)
                }
            delay(80)
            job.cancel()
            job.join()
            assertEquals("ab", result)
        }
}
