import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import java.util.Collections
import java.util.UUID
import kotlin.random.Random
import kotlin.test.Test

class SyncTest {
    @Test
    fun `Synchronizes commands`() =
        runBlocking {
            // Create a channel to synchronize
            val syncChannel1 = Channel<Unit>(1)
            val syncChannel2 = Channel<Unit>(1)

            // Launch two parallel jobs
            val job1 =
                launch {
                    println("Job 1 started")
                    delay(1000) // Simulate work
                    println("Job 1 reached sync point")
                    syncChannel1.send(Unit) // Send signal to sync point
                    syncChannel2.receive() // Wait for Job 2 to sync up
                    println("Job 1 resumed after sync")
                    // Continue work after sync
                }

            val job2 =
                launch {
                    println("Job 2 started")
                    delay(500) // Simulate work
                    println("Job 2 reached sync point")
                    syncChannel2.send(Unit) // Send signal to sync point
                    syncChannel1.receive() // Wait for Job 1 to sync up
                    println("Job 2 resumed after sync")
                    // Continue work after sync
                }

            // Wait for both jobs to finish
            job1.join()
            job2.join()

            println("Both jobs completed")
        }

    @Test
    fun `a`() =
        runTest {
            val channelsRaw = hashMapOf<String, Channel<Unit>>()
            val channels = Collections.synchronizedMap(channelsRaw)

            val jobs =
                List(10) { UUID.randomUUID().toString() }
                    .mapIndexed { index, id ->
                        channels[id] = Channel(0)
                        val run: (suspend CoroutineScope.() -> Unit) = {
                            println("Job $id started")
                            delay(Random.nextLong(500, 1500))
//                        channels[index].send(Unit)
                            val sync = Random.nextBoolean()
                            if (sync) {
                                println("Job $id reached sync point")
                                repeat(channels.size - 1) {
                                    channels[id]?.trySend(Unit)
                                }
                                channels.filter { (key, _) -> key != id }.forEach { it.value.receiveCatching() }
                                while (true) {
                                    if (channels[id]?.receiveCatching()?.isSuccess == true) break
                                }
                                println("Job $id resumed after sync")
                                delay(Random.nextLong(500, 1500))
                            } else {
                                delay(Random.nextLong(250, 500))
                            }
                            println("Job $id completed | $sync")
                        }
                        return@mapIndexed run to id
                    }

            jobs.forEach {
                channels[it.second] = Channel(channels.size - 1)
                launch {
                    val a = it.first
                    a()
                    channels[it.second]?.close()
                    channels.remove(it.second)
                }
            }
        }
}
