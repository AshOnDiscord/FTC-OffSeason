package com.millburnx.cmdx

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

public interface ICommand {
    public suspend fun run(scope: CoroutineScope)

    public fun cancel()
}

public abstract class Command(
    public val name: String,
    public val onCancel: () -> Unit = {},
    public val runnable: suspend () -> Unit,
) : ICommand {
    private val synchChanel = Channel<Unit>()
    public lateinit var job: Job

    @OptIn(DelicateCoroutinesApi::class)
    public suspend fun synch(): Boolean {
        synchChanel.send(Unit)
        return !synchChanel.isClosedForReceive
    }

    public override suspend fun run(scope: CoroutineScope) {
        job =
            scope.launch {
                try {
                    println("Command: $name started.")
                    runnable()
                } catch (e: CancellationException) {
                    println("Command $name canceled.")
                    onCancel()
                } finally {
                    println("Command $name completed.")
                    synchChanel.close()
                }
            }

        job.join()
    }

    public override fun cancel() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}
