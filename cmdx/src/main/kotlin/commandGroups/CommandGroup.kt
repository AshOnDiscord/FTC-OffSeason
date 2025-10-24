package com.millburnx.cmdx.commandGroups

import com.millburnx.cmdx.Command
import com.millburnx.cmdx.CommandID
import com.millburnx.cmdx.ICommand
import com.millburnx.cmdx.NonSyncableCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Collections
import kotlin.collections.forEach

public abstract class CommandGroup(
    public override val name: String,
) : ICommand, NonSyncableCommand {
    protected val _commands: MutableList<ICommand> = Collections.synchronizedList(mutableListOf())
    public val commands: List<ICommand> get() = _commands.toList()

    public var currentScope: CoroutineScope? = null

    override var parentGroup: CommandGroup? = null
        set(value) {
            if (field == value) return


            val locks = listOfNotNull(field?.mutex, value?.mutex)
                .distinct()
                .sortedBy { System.identityHashCode(it) } // prevent deadlocks

//            runBlocking {
//                locks.forEach { it.lock() }
            // when we are moved into a different group, we need to update all the channel/lists
            if (value != null) {
                value.channels += this@CommandGroup.localChannels
                value.commandList += this@CommandGroup.commandList

                if (field != null) {
//                        this@CommandGroup.channels.forEach { field!!.channels.remove(it.key) }
                    this@CommandGroup.channels.toMap().forEach { field!!.channels.remove(it.key) }
                    field!!.commandList -= this@CommandGroup.commandList.toSet()
                }
            }
//                locks.reversed().forEach { it.unlock() }
//            }

            field = value
        }

    private val localCommandList: MutableList<CommandID> = Collections.synchronizedList(mutableListOf())
    private val localChannels: MutableMap<CommandID, Channel<Unit>> = hashMapOf()
    public val channels: MutableMap<CommandID, Channel<Unit>> get() = parentGroup?.channels ?: localChannels
    public val commandList: MutableList<CommandID> get() = parentGroup?.commandList ?: localCommandList

    private val _mutex = Mutex()
    public val mutex: Mutex
        get() = parentGroup?.mutex ?: _mutex

    private var _onSync: () -> Unit = {}
    public var onSync: () -> Unit
        get() = parentGroup?.onSync ?: _onSync
        set(value: () -> Unit) {
            if (parentGroup != null) parentGroup?.onSync = value
            _onSync = value
        }


    internal open fun addCommand(command: ICommand) {
//        runBlocking {
//            mutex.withLock {
        _commands.add(command)
//            }
//        }
        command.parentGroup = this
    }

    public operator fun ICommand.unaryPlus(): Unit = addCommand(this)

    // suppressed since this is supposed to be a constructor mock
    @Suppress("ktlint:standard:function-naming", "detekt:FunctionNaming", "FunctionName")
    public fun Command(
        name: String = "Unnamed Command",
        onCancel: () -> Unit = {},
        block: suspend Command.() -> Unit,
    ) {
        addCommand(object : Command(name, onCancel, block) {})
    }

    private fun isReady(self: CommandID): Boolean {
        val currentlyWaiting = channels.keys + self
        val required = commandList.toSet()

        return currentlyWaiting.containsAll(required)
    }

    internal suspend fun syncChild(child: ICommand) {
        println("Job ${child.name} (${child.id}) reached sync point")
        val waiting = mutex.withLock {
            if (isReady(child.id)) {
                // alert others
                println("All others are ready, ${child.id} is notifying others")

                // trigger onsync callback first
                onSync()

//                channels.entries.toList().forEach {
                channels.toMap().forEach {
                    if (it.key == child.id) return@forEach
                    channels.remove(it.key)
                    it.value.trySend(Unit)
                }
                return@withLock null
            } else {
                // wait for ready signal
                val channel = Channel<Unit>(1)
                channels[child.id] = channel
                return@withLock channel
            }
        }
        if (waiting != null) {
            try {
                waiting.receive()
            } finally {
                waiting.close()
            }
        }
        println("Job ${child.name} (${child.id}) resumed after sync")
    }

    public suspend fun cleanUp(command: ICommand) {
        println("Job ${command.name} (${command.id}) beginning clean up")
        mutex.withLock {
            commandList.remove(command.id)
            channels.remove(command.id)

            if (isReady(command.id) && commandList.isNotEmpty()) {
                println("Job ${command.id} is holding others up, notifying others")
                // notify others
//                channels.entries.toList().forEach {
                onSync()
                channels.toMap().forEach {
                    if (it.key == command.id) return@forEach
                    channels.remove(it.key)
                    it.value.trySend(Unit)
                }
            }
        }
        println("Job ${command.name} (${command.id}) cleared")
    }

    public abstract override suspend fun run(scope: CoroutineScope)

    public override fun cancel() {
        _commands.forEach { it.cancel() }
        currentScope?.cancel()
    }
}