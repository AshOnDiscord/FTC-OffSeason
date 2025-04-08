package com.millburnx.cmdx.commandGroups

import com.millburnx.cmdx.Command
import com.millburnx.cmdx.ICommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import java.util.Collections
import kotlin.collections.forEach

public abstract class CommandGroup(
    public override val name: String,
) : ICommand {
    protected val commands: MutableList<ICommand> = mutableListOf()

    public var currentScope: CoroutineScope? = null
    override var parentGroup: CommandGroup? = null

    public val channelsRaw: MutableMap<String, Channel<Unit>> = hashMapOf<String, Channel<Unit>>()
    public val channels: MutableMap<String, Channel<Unit>> = Collections.synchronizedMap(channelsRaw)

    internal fun addCommand(command: ICommand) {
        commands.add(command)
        channels[command.hashCode().toString()] = Channel(0)
        command.parentGroup = this
    }

    public operator fun ICommand.unaryPlus() = addCommand(this)

    // suppressed since this is supposed to be a constructor mock
    @Suppress("ktlint:standard:function-naming", "detekt:FunctionNaming")
    public fun Command(
        name: String = "Unnamed Command",
        onCancel: () -> Unit = {},
        block: suspend Command.() -> Unit,
    ) {
        addCommand(object : Command(name, onCancel, block) {})
    }

    internal suspend fun synchChild(child: ICommand) {
        val id = child.hashCode().toString()
        println("Job ${child.name} ($id) reached sync point")
        repeat(channels.size - 1) {
            channels[id]?.trySend(Unit)
        }
        channels.filter { (key, _) -> key != id }.forEach { it.value.receiveCatching() }
        channels[id]?.close()
        channels[id] = Channel(channels.size - 1)
        println("Job ${child.name} ($id) resumed after sync")
    }

    protected fun setupSync() {
        channels.forEach { (key, value) ->
            channels[key] = Channel(channels.size - 1)
        }
    }

    protected fun cleanUp(command: ICommand) {
        val id = command.hashCode().toString()
        channels[id]?.close()
        channels.remove(id)
    }

    public abstract override suspend fun run(scope: CoroutineScope)

    public override fun cancel() {
        commands.forEach { it.cancel() }
        currentScope?.cancel()
    }
}
