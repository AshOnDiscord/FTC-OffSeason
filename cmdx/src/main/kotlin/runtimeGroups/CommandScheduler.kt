package com.millburnx.cmdx.runtimeGroups

import com.millburnx.cmdx.ICommand
import com.millburnx.cmdx.commandGroups.Parallel
import com.sun.tools.javac.code.TypeAnnotationPosition.field
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public class CommandScheduler(
    name: String = "Unnamed Command Scheduler",
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    onSync: () -> Unit = {},
) {
    private val scope = CoroutineScope(dispatcher)
    public val runner: Parallel = Parallel(name) {}
    public var onSync: () -> Unit = onSync
        set(value) {
            field = value
            runner.onSync = value
        }

    public fun schedule(command: ICommand) {
        runner.addCommand(command)
        scope.launch {
            // immediately run command on added
            command.run(scope)
        }
    }
}
