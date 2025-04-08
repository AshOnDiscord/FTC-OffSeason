package com.millburnx.cmdx

import com.millburnx.cmdx.commandGroups.CommandGroup
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

public interface ICommand {
    public val name: String
    public var parentGroup: CommandGroup?

    public suspend fun run(scope: CoroutineScope)

    public fun cancel()
}

public open class Command(
    public override val name: String = "Unnamed Command",
    public val onCancel: () -> Unit = {},
    public val runnable: suspend Command.() -> Unit,
) : ICommand {
    override var parentGroup: CommandGroup? = null
    public lateinit var job: Job

    public suspend fun sync() {
        parentGroup?.syncChild(this)
    }

    public override suspend fun run(scope: CoroutineScope) {
        job =
            scope.launch {
                try {
                    parentGroup?.channels[this@Command.hashCode().toString()] = Channel(Channel.UNLIMITED)
                    println("Command: $name started.")
                    runnable()
                } catch (e: CancellationException) {
                    println("Command $name canceled.")
                    onCancel()
                } finally {
                    println("Command $name completed.")
                    parentGroup?.cleanUp(this@Command)
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
