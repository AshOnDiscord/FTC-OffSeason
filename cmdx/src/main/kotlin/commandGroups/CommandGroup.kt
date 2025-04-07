package com.millburnx.cmdx.commandGroups

import com.millburnx.cmdx.Command
import com.millburnx.cmdx.ICommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlin.collections.forEach

public abstract class CommandGroup(
    public val name: String,
) : ICommand {
    protected val commands: MutableList<ICommand> = mutableListOf()

    public var currentScope: CoroutineScope? = null
    private val syncMutex = Mutex()
    private var syncCounter = 0
    private val syncChannel = Channel<Unit>(Channel.UNLIMITED)
    override var parentGroup: CommandGroup? = null

    public operator fun ICommand.unaryPlus() {
        commands.add(this)
        this.parentGroup = this@CommandGroup
    }

    // suppressed since this is supposed to be a constructor mock
    @Suppress("ktlint:standard:function-naming", "detekt:FunctionNaming")
    public fun Command(
        name: String,
        onCancel: () -> Unit = {},
        block: suspend Command.() -> Unit,
    ) {
        commands.add(object : Command(name, onCancel, block) {})
    }

    internal suspend fun groupSync() {
        syncChannel.receive()
    }

    public abstract override suspend fun run(scope: CoroutineScope)

    public override fun cancel() {
        commands.forEach { it.cancel() }
        currentScope?.cancel()
    }
}
