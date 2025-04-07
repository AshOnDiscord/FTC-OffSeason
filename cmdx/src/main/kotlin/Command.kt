package com.millburnx.cmdx

import com.millburnx.cmdx.commandGroups.CommandGroup
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public interface ICommand {
    public var parentGroup: CommandGroup?

    public suspend fun run(scope: CoroutineScope)

    public fun cancel()
}

public open class Command(
    public val name: String = "Unnamed Command",
    public val onCancel: () -> Unit = {},
    public val runnable: suspend Command.() -> Unit,
) : ICommand {
    override var parentGroup: CommandGroup? = null
    public lateinit var job: Job

    public suspend fun synch() {
        parentGroup?.groupSync()
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

public class EmptyCommand : Command("Empty Command", {}, {}) {
    override suspend fun run(scope: CoroutineScope) {}
}
