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

    private val channelsRaw: MutableMap<String, Channel<Unit>> = hashMapOf<String, Channel<Unit>>()
    private val localChannels: MutableMap<String, Channel<Unit>> = Collections.synchronizedMap(channelsRaw)
    public val channels: MutableMap<String, Channel<Unit>>
        get() = if (parentGroup != null) parentGroup!!.channels else localChannels

    internal open fun addCommand(command: ICommand) {
        commands.add(command)
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

    internal suspend fun syncChild(child: ICommand) {
        val id = child.hashCode().toString()
        println("Job ${child.name} ($id) reached sync point")
        repeat(channels.size - 1) {
            channels[id]?.trySend(Unit)
        }
        channels.filter { (key, _) -> key != id }.forEach { it.value.receiveCatching() }
        channels[id]?.close()
        channels[id] = Channel(Channel.UNLIMITED)
        println("Job ${child.name} ($id) resumed after sync")
    }

    public fun cleanUp(command: ICommand) {
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
