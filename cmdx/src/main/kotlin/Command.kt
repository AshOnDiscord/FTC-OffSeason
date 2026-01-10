package com.millburnx.cmdx

import com.millburnx.cmdx.commandGroups.CommandGroup
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock

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
    public var job: Job? = null

    public suspend fun sync() {
        parentGroup?.syncChild(this)
    }

    public override suspend fun run(scope: CoroutineScope) {
        val job =
            scope.launch {
                try {
                    parentGroup?.mutex?.withLock {
                        parentGroup?.commandList?.add(id)
                    }
                    Settings.debugLog("Command: $name started.")
                    runnable()
                } catch (e: CancellationException) {
                    Settings.debugLog("Command $name canceled.")
                    onCancel()
                } finally {
                    Settings.debugLog("Command $name completed.")
                    parentGroup?.cleanUp(this@Command)
                }
            }
        this.job = job
        job.join()
    }

    public override fun cancel() {
        job?.cancel()
    }
}

public typealias CommandS = Command

public class EmptyCommand : Command("Empty Command", {}, {}) {
    override suspend fun run(scope: CoroutineScope) {
        return
    }
}
