
import com.millburnx.cmdx.commandGroups.Parallel
import com.millburnx.cmdx.commandGroups.Sequential
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandGroupTest {
    @Test
    fun `Sequential and Parallel can be inter-nested`() {
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

        runTest {
            sequence.run(this)
        }

        assertEquals("abcdefg", result)
    }

    @Test
    fun `Inter-nested CommandGroups can be cancelled`() =
        runTest {
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

            val job =
                launch {
                    sequence.run(this)
                }
            delay(50)
            sequence.cancel()
            job.join()

            assertEquals("ab", result)
        }

    @Test
    fun `Inter-nested CommandGroups can be cancelled when scope is cancelled`() =
        runTest {
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

            val job =
                launch {
                    sequence.run(this)
                }

            delay(50)
            job.cancel()
            job.join()

            assertEquals("ab", result)
        }
}
