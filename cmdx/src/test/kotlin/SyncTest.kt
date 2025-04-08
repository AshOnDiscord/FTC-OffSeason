import com.millburnx.cmdx.Command
import com.millburnx.cmdx.CommandS
import com.millburnx.cmdx.commandGroups.Parallel
import com.millburnx.cmdx.commandGroups.ParallelDeadline
import com.millburnx.cmdx.commandGroups.ParallelRace
import com.millburnx.cmdx.commandGroups.Sequential
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SyncTest {
    @Test
    fun `Synchronizes commands`() {
        var result = ""
        var sync =
            Parallel {
                Command("ad") {
                    delay(25)
                    result += "a"
                    sync()
                    delay(25)
                    result += "d"
                }

                Command("bf") {
                    delay(50)
                    result += "b"
                    sync()
                    delay(75)
                    result += "f"
                }

                Command("ce") {
                    delay(75)
                    result += "c"
                    sync()
                    delay(50)
                    result += "e"
                }
            }

        runTest {
            sync.run(this)
        }

        assertEquals("abcdef", result)
    }

    @Test
    fun `Nested Synchronizes commands`() {
        var result = ""
        var sync =
            Parallel {
                +Parallel {
                    Command("af") {
                        delay(25)
                        result += "a"
                        sync()
                        delay(75)
                        result += "f"
                    }
                    Command("bd") {
                        delay(50)
                        result += "b"
                        sync()
                        delay(25)
                        result += "d"
                    }
                }
                Command("ce") {
                    delay(75)
                    result += "c"
                    sync()
                    delay(50)
                    result += "e"
                }
            }

        runTest {
            sync.run(this)
        }

        assertEquals("abcdef", result)
    }

    @Test
    fun `Syncs commands of varying lengths`() {
        var result = ""
        var sync =
            Parallel {
                Command("adf") {
                    delay(25)
                    result += "a"
                    sync()
                    delay(25)
                    result += "d"
                    sync()
                    delay(75)
                    result += "f"
                }
                Command("ce") {
                    delay(75)
                    result += "c"
                    sync()
                    delay(50)
                    result += "e"
                }
                Command("b") {
                    delay(50)
                    result += "b"
                }
            }

        runTest {
            sync.run(this)
        }
        assertEquals("abcdef", result)
    }

    @Test
    fun `Sync works with varying command groups`() {
        var result = ""

        var sync =
            Parallel("Parallel") {
                +Sequential("Sequential") {
                    Command {
                        result += "1"
                        delay(25)
                        sync()
                        delay(25)
                        result += "4"
                    }
                    +ParallelDeadline(
                        "DeadlineParent",
                        CommandS("deadline") {
                            delay(50)
                            result += "6"
                            sync()
                            delay(25)
                            result += "8"
                            sync()
                            delay(75)
                            result += "c"
                        },
                    ) {
                        Command("short") {
                            delay(25)
                            result += "5"
                            sync()
                            delay(75)
                            result += "a"
                        }
                        Command("long") {
                            delay(75)
                            result += "7"
                            sync()
                            delay(50)
                            result += "9"
                            sync()
                            delay(25)
                            result += "b"
                            sync()
                            delay(25)
                            result += " "
                        }
                    }
                }
                +ParallelRace("Race") {
                    Command("Race Long") {
                        delay(25)
                        result += "2"
                        sync()
                        delay(25)
                        result += " "
                    }
                    Command("Race Short") {
                        delay(50)
                        result += "3"
                    }
                }
            }

        runTest {
            sync.run(this)
        }

        assertEquals("123456789abc", result)
    }

    fun `Standalone Command does not break`() {
        var result = ""
        var sync =
            Command("standalone") {
                delay(25)
                result += "a"
                sync()
                delay(25)
                result += "b"
            }
        runTest {
            sync.run(this)
        }
        assertEquals("ab", result)
    }
}
