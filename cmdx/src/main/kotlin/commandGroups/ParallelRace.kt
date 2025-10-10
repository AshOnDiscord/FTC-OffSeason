package com.millburnx.cmdx.commandGroups

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

public class ParallelRace(
    name: String = "Unnamed ParallelRace",
    block: ParallelRace.() -> Unit,
) : CommandGroup(name) {
    init {
        block()
    }

    public override suspend fun run(scope: CoroutineScope) {
        val finished = CompletableDeferred<Unit>()
        _commands.forEach {
            scope.launch {
                it.run(scope)
                finished.complete(Unit)
            }
        }

        finished.await()
        cancel()
    }
}
