package com.millburnx.cmdx

import com.millburnx.cmdx.Command
import com.millburnx.cmdx.commandGroups.CommandGroup
import kotlinx.coroutines.CoroutineScope
import com.millburnx.cmdx.Command as CmdxCommand

public class Conditional(
    name: String,
    public val condition: () -> Boolean,
    public val ifTrue: Command,
    public val ifFalse: Command = EmptyCommand(),
) : CommandGroup(name) {
    public constructor(
        condition: () -> Boolean,
        ifTrue: Command,
        ifFalse: Command = EmptyCommand(),
    ) : this("Unnamed Conditional", condition, ifTrue, ifFalse)

    public constructor(
        name: String,
        condition: () -> Boolean,
        ifTrue: () -> Unit,
        ifFalse: () -> Unit,
    ) : this(name, condition, CmdxCommand(name, {}, ifTrue), CmdxCommand(name, {}, ifFalse))

    public constructor(
        condition: () -> Boolean,
        ifTrue: () -> Unit,
        ifFalse: () -> Unit,
    ) : this("Unnamed Conditional", condition, CmdxCommand("", {}, ifTrue), CmdxCommand("", {}, ifFalse))

    public constructor(
        name: String,
        condition: () -> Boolean,
        ifTrue: () -> Unit,
    ) : this(name, condition, CmdxCommand(name, {}, ifTrue), EmptyCommand())

    public constructor(
        condition: () -> Boolean,
        ifTrue: () -> Unit,
    ) : this("Unnamed Conditional", condition, CmdxCommand("", {}, ifTrue), EmptyCommand())

    override suspend fun run(scope: CoroutineScope) {
        // runtime evulation of the condition
        val command = if (condition()) ifTrue else ifFalse
        commands.clear()
        commands.add(command)
        command.run(scope)
    }
}
