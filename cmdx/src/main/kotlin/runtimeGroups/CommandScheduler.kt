package com.millburnx.cmdx.runtimeGroups

import com.millburnx.cmdx.ICommand
import com.millburnx.cmdx.commandGroups.Parallel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public class CommandScheduler(
    name: String = "Unnamed Command Scheduler",
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher)
    public val runner: Parallel = Parallel(name) {}

    public suspend fun schedule(command: ICommand) {
        runner.addCommand(command)
        scope.launch {
            // immediately run command on added
            command.run(scope)
        }
    }
}
