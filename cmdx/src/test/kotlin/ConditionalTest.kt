import com.millburnx.cmdx.Command
import com.millburnx.cmdx.Conditional
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ConditionalTest {
    @Test
    fun `True conditional runs if condition is true`() {
        var result = ""
        val conditional =
            Conditional("conditional", { true }, {
                result += "a"
            }, {
                result += "b"
            })
        runTest { conditional.run(this) }
        assert(result == "a")
    }

    @Test
    fun `False conditional runs if condition is false`() {
        var result = ""
        val conditional =
            Conditional({ false }, {
                result += "a"
            }, {
                result += "b"
            })
        runTest { conditional.run(this) }
        assert(result == "b")
    }

    @Test
    fun `If only one conditional is provided, it will be run as true case`() {
        var result = ""
        val conditional =
            Conditional("conditional", { true }, {
                result += "a"
            })
        runTest { conditional.run(this) }
        assert(result == "a")

        result = ""
        val conditional2 =
            Conditional("conditional", { false }, {
                result += "a"
            })
        runTest { conditional2.run(this) }
        assert(result == "")

        result = ""
        val conditional3 =
            Conditional("conditional", { true }) {
                result += "a"
            }
        runTest { conditional3.run(this) }
        assert(result == "a")

        result = ""
        val conditional4 =
            Conditional("conditional", { false }) {
                result += "a"
            }
        runTest { conditional4.run(this) }
        assert(result == "")

        result = ""
        val conditional5 =
            Conditional(
                { true },
                Command {
                    result += "a"
                },
            )
        runTest { conditional5.run(this) }
        assert(result == "a")

        result = ""
        val conditional6 =
            Conditional(
                { false },
                Command {
                    result += "a"
                },
            )
        runTest { conditional6.run(this) }
        assert(result == "")

        result = ""
        val conditional7 =
            Conditional({ true }) {
                result += "a"
            }
        runTest { conditional7.run(this) }
        assert(result == "a")

        result = ""
        val conditional8 =
            Conditional({ false }) {
                result += "a"
            }

        runTest { conditional8.run(this) }
        assert(result == "")
    }

    @Test
    fun `Cancels correctly`() =
        runTest {
            var result = ""
            var canceled = ""
            val conditional =
                Conditional(
                    { false },
                    Command("true", {
                        canceled += "a"
                    }) {
                        delay(100)
                        result += "a"
                    },
                    Command("false", {
                        canceled += "b"
                        println("Canceled")
                    }) {
                        delay(100)
                        result += "b"
                    },
                )
            val job = launch { conditional.run(this) }
            delay(50)
            println("canceling...")
            conditional.cancel()
            job.join()
            assert(canceled == "b")
        }

    @Test
    fun `Cancels correctly with scope`() =
        runTest {
            var result = ""
            var canceled = ""
            val conditional =
                Conditional(
                    "conditional",
                    { false },
                    Command("true", {
                        canceled += "a"
                    }) {
                        delay(100)
                        result += "a"
                    },
                    Command("false", {
                        canceled += "b"
                    }) {
                        delay(100)
                        result += "b"
                    },
                )
            val job = launch { conditional.run(this) }
            delay(50)
            job.cancel()
            job.join()
            assert(canceled == "b")
        }
}
