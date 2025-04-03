import com.millburnx.cmdx.commandGroups.ParallelRace
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ParallelRaceTest {
    @Test
    fun `ParallelRace runs commands in parallel`() {
        var result = ""
        val parallelRace =
            ParallelRace {
                Command("a") {
                    delay(20)
                    result += "a"
                    delay(100)
                }
                Command("b") {
                    delay(100)
                    result += "b"
                }
            }

        runTest {
            parallelRace.run(this)
        }
        assert(result == "ab")
    }

    @Test
    fun `ParallelRace cancels others after winner`() {
        var result = ""
        val parallelRace =
            ParallelRace {
                Command("a") {
                    delay(20)
                    result += "a"
                    delay(100)
                    result += "c"
                }
                Command("b") {
                    delay(100)
                    result += "b"
                }
            }

        runTest {
            parallelRace.run(this)
        }
        assert(result == "ab")
    }

    @Test
    fun `ParallelRace cancel commands`() =
        runTest {
            var result = ""
            val parallelRace =
                ParallelRace {
                    Command("a") {
                        delay(20)
                        result += "a"
                        delay(100)
                        result += "c"
                    }
                    Command("b") {
                        delay(100)
                        result += "b"
                    }
                }

            val job =
                launch {
                    parallelRace.run(this)
                }
            delay(50)
            parallelRace.cancel()
            job.join()
            assert(result == "a")
        }

    @Test
    fun `ParallelRace cancels commands when scope is cancelled`() =
        runTest {
            var result = ""
            val parallelRace =
                ParallelRace {
                    Command("a") {
                        delay(20)
                        result += "a"
                        delay(100)
                        result += "c"
                    }
                    Command("b") {
                        delay(100)
                        result += "b"
                    }
                }
            val job =
                launch {
                    parallelRace.run(this)
                }
            delay(50)
            job.cancel()
            job.join()
            assert(result == "a")
        }
}
