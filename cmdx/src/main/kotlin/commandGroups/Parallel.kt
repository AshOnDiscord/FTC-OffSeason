package com.millburnx.cmdx.commandGroups

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public class Parallel(
    name: String = "Unnamed Parallel",
    public val block: Parallel.() -> Unit,
) : CommandGroup(name) {
    init {
        block()
    }

    public override suspend fun run(scope: CoroutineScope) {
        this.setupSync()
        currentScope = scope
        val jobs: List<Job> =
            commands.map {
                scope.launch {
                    it.run(this)
                    this@Parallel.cleanUp(it)
                }
            }

        jobs.forEach { it.join() }
    }
}
