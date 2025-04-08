import com.millburnx.cmdx.Command
import com.millburnx.cmdx.commandGroups.Parallel
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
                    synch()
                    delay(25)
                    result += "d"
                }

                Command("bf") {
                    delay(50)
                    result += "b"
                    synch()
                    delay(75)
                    result += "f"
                }

                Command("ce") {
                    delay(75)
                    result += "c"
                    synch()
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
                        synch()
                        delay(75)
                        result += "f"
                    }
                    Command("bd") {
                        delay(50)
                        result += "b"
                        synch()
                        delay(25)
                        result += "d"
                    }
                }
                Command("ce") {
                    delay(75)
                    result += "c"
                    synch()
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
    fun `Synchs commands of varying lengths`() {
        var result = ""
        var sync =
            Parallel {
                Command("") {
                    delay(25)
                    result += "a"
                    synch()
                    delay(25)
                    result += "d"
                    synch()
                    delay(75)
                    result += "f"
                }
                Command("") {
                    delay(75)
                    result += "c"
                    synch()
                    delay(50)
                    result += "e"
                }
                Command("") {
                    delay(50)
                    result += "b"
                }
            }

        runTest {
            sync.run(this)
        }
        assertEquals("abcdef", result)
    }
}
