package com.millburnx.cmdx.commandGroups

import com.millburnx.cmdx.Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.collections.forEach

public abstract class CommandGroup(
    public val name: String,
) {
    protected val commands: MutableList<Command> = mutableListOf()

    public var currentScope: CoroutineScope? = null

    public operator fun Command.unaryPlus() {
        commands.add(this)
    }

    // suppressed since this is supposed to be a constructor mock
    @Suppress("ktlint:standard:function-naming", "detekt:FunctionNaming")
    public fun Command(
        name: String,
        onCancel: () -> Unit = {},
        block: suspend () -> Unit,
    ) {
        commands.add(object : Command(name, onCancel, block) {})
    }

    public abstract suspend fun run(scope: CoroutineScope)

    public fun cancel() {
        commands.forEach { it.cancel() }
        currentScope?.cancel()
    }
}
