package com.millburnx.cmdx.commandGroups

import com.millburnx.cmdx.Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

public class ParallelDeadline(
    name: String,
    public val deadline: Command,
    public val block: ParallelDeadline.() -> Unit,
) : CommandGroup(name) {
    public constructor(deadline: Command, block: ParallelDeadline.() -> Unit) :
        this("Unnamed ParallelDeadline", deadline, block)

    init {
        block()
        deadline.parentGroup = this
    }

    public override suspend fun run(scope: CoroutineScope) {
        val jobs =
            commands.map {
                scope.launch {
                    it.run(this)
                }
            }

        deadline.run(scope)
        jobs.forEach { it.cancel() }
    }
}
