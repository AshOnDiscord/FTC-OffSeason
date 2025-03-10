import com.millburnx.cmdx.commandGroups.Parallel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ParallelTest {
    @Test
    fun `parallel runs commands in parallel`() {
        var result = ""
        val parallel =
            Parallel("parallel") {
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
            parallel.run(this)
        }

        assertEquals("abc", result)
    }

    @Test
    fun `parallel cancels commands`() =
        runTest {
            var result = ""
            val parallel =
                Parallel("parallel.cancel") {
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
                    parallel.run(this)
                }

            delay(65)
            parallel.cancel()
            job.join()

            assertEquals("a", result)
        }

    @Test
    fun `parallel cancels commands when scope is cancelled`() =
        runTest {
            var result = ""
            val parallel =
                Parallel("scope.cancel") {
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
                    parallel.run(this)
                }

            delay(65)
            job.cancel()
            job.join()

            assertEquals("a", result)
        }

    @Test
    fun `Handle nested parallel commands`() {
        var result = ""
        val parallel =
            Parallel("nested.parallel") {
                Command("c") {
                    delay(100)
                    result += "c"
                }
                +Parallel("nested.parallel") {
                    Command("a") {
                        delay(50)
                        result += "a"
                    }
                    Command("b") {
                        delay(75)
                        result += "b"
                    }
                }
            }

        runTest {
            parallel.run(this)
        }

        assertEquals("abc", result)
    }

    @Test
    fun `parallel cancels nested command groups`() =
        runTest {
            var result = ""
            val parallel =
                Parallel("nested.parallel.cancel") {
                    Command("c") {
                        delay(100)
                        result += "c"
                    }
                    +Parallel("nested.parallel.cancel") {
                        Command("a") {
                            delay(50)
                            result += "a"
                        }
                        Command("b") {
                            delay(75)
                            result += "b"
                        }
                    }
                }

            val job =
                launch {
                    parallel.run(this)
                }

            delay(65)
            parallel.cancel()
            job.join()

            assertEquals("a", result)
        }

    @Test
    fun `parallel cancels nested command groups when scope is cancelled`() =
        runTest {
            var result = ""
            val parallel =
                Parallel("nested.parallel.cancel") {
                    Command("c") {
                        delay(100)
                        result += "c"
                    }
                    +Parallel("nested.parallel.cancel") {
                        Command("a") {
                            delay(50)
                            result += "a"
                        }
                        Command("b") {
                            delay(75)
                            result += "b"
                        }
                    }
                }

            val job =
                launch {
                    parallel.run(this)
                }

            delay(65)
            job.cancel()
            job.join()

            assertEquals("a", result)
        }
}
