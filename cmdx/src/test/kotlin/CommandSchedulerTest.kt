import com.millburnx.cmdx.Command
import com.millburnx.cmdx.commandGroups.Parallel
import com.millburnx.cmdx.commandGroups.Sequential
import com.millburnx.cmdx.runtimeGroups.CommandScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandSchedulerTest {
    @Test
    fun `CommandScheduler runs and syncs commands`() =
        runTest {
            var result = ""
            var scheduler = CommandScheduler(dispatcher = StandardTestDispatcher(testScheduler))
            scheduler.schedule(
                Command("b c") {
                    delay(100)
                    result += "b"
                    sync()
                    delay(25)
                    result += "c"
                },
            )
            scheduler.schedule(
                Command("a d g") {
                    delay(50)
                    result += "a"
                    sync()
                    delay(50)
                    result += "d"
                    sync()
                    delay(75)
                    result += "g"
                },
            )
            delay(110)
            assertEquals("ab", result)
            scheduler.schedule(
                Command("ef") {
                    delay(50)
                    result += "e"
                    sync()
                    delay(50)
                    result += "f"
                },
            )
            delay(60)
            assertEquals("abcde", result)
            delay(100)
            assertEquals("abcdefg", result)
        }

    @Test
    fun `CommandScheduler runs command groups`() =
        runTest {
            var result = ""
            var scheduler = CommandScheduler(dispatcher = StandardTestDispatcher(testScheduler))

            scheduler.schedule(
                Parallel {
                    Command("1 4") {
                        delay(25)
                        result += "1"
                        sync()
                        delay(75)
                        result += "4"
                    }
                    Command("3 6") {
                        sync()
                        delay(50)
                        result += "3"
                        sync()
                        delay(75)
                        result += "6"
                    }
                },
            )
            delay(30)
            assertEquals("1", result)
            scheduler.schedule(
                Sequential {
                    Command("2") {
                        result += "2"
                    }
                    Command("wait") {
                        sync()
                        delay(50)
                    }
                    Command("5") {
                        result += "5"
                    }
                },
            )
            delay(75)
            assertEquals("1234", result)
            delay(100)
            assertEquals("123456", result)
        }

    @Test
    fun `CommandScheduler continues running despite subcommand cancels`() = runTest {
        var result = ""
        var scheduler = CommandScheduler(dispatcher = StandardTestDispatcher(testScheduler))

        val command1 = Command("a d") {
            delay(10)
            sync()
            delay(10)
            result += "a"
            sync()
            delay(50)
            result += "d"
            delay(50)
            // cancel command
            sync()
            result += "ERROR"
        }

        val command2 = Command("b c e") {
            delay(10)
            sync()
            delay(50)
            result += "b"
            sync()
            delay(20)
            result += "c"
            sync()
            result += "e"
        }

        scheduler.schedule(command1)
        scheduler.schedule(command2)

        delay(10 + 50 + 70)
        command1.cancel()
        delay(100)
        assertEquals("abcde", result)
    }

    @Test
    fun `CommandScheduler continues running despite subcommand cancels - v2`() = runTest {
        var result = ""
        var scheduler = CommandScheduler(dispatcher = StandardTestDispatcher(testScheduler))

        val command1 = Command("a c", {
            println("canceling | $result")
        }) {
            delay(10)
            sync()
            delay(10)
            result += "a"
            sync()
            println(result)
            delay(20)
            result += "c"
            delay(50)
            // cancel command
            sync()
            result += "ERROR"
        }

        val command2 = Command("b d e") {
            delay(10)
            sync()
            delay(50)
            result += "b"
            sync()
            delay(50)
            result += "d"
            sync()
            result += "e"
        }

        scheduler.schedule(command1)
        scheduler.schedule(command2)

        delay(10 + 50 + 30)
        command1.cancel()
        delay(100)
        assertEquals("abcde", result)
    }

    @Test
    fun `CommandScheduler calls oncancel`() = runTest {
        var result = ""
        var scheduler = CommandScheduler(dispatcher = StandardTestDispatcher(testScheduler))
        val command1 = Command("a c", {
            println("canceling")
            result += "b"
        }) {
            delay(10)
            result += "a"
            sync()
            delay(50)
            result += "ERROR"
        }

        scheduler.schedule(command1)
        delay(20)
        command1.cancel()
        delay(50)
        assertEquals("ab", result)
    }
}
