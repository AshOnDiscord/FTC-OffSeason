package com.millburnx.cmdx

import com.millburnx.cmdx.commandGroups.CommandGroup
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

public interface ICommand {
    public val name: String
    public val id: CommandID
        get() = this.hashCode().toString()
    public var parentGroup: CommandGroup?

    public suspend fun run(scope: CoroutineScope)

    public fun cancel()
}

public interface NonSyncableCommand : ICommand {}

public typealias CommandID = String;

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
//                    parentGroup?.channels[this@Command.id] = Channel(Channel.UNLIMITED)
//                    if (command !is NonSyncableCommand) commandList.add(command.id) // make sure its syncable, aka not a group
                    parentGroup?.commandList?.add(id)
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

public typealias CommandS = Command

public class EmptyCommand : Command("Empty Command", {}, {}) {
    override suspend fun run(scope: CoroutineScope) {
        return
    }
}
