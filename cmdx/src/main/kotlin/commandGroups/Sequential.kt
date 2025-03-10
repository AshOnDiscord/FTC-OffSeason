package com.millburnx.cmdx.commandGroups

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive

public class Sequential(
    name: String,
    public val block: Sequential.() -> Unit,
) : CommandGroup(name) {
    init {
        block()
    }

    public override suspend fun run(scope: CoroutineScope) {
        currentScope = scope
        println("Running Sequential Command Group: $name")
        for (command in commands) {
            if (!scope.isActive) {
                cancel()
                break
            }
            command.run(scope)
        }
        println("Sequential Command Group $name completed.")
    }
}
