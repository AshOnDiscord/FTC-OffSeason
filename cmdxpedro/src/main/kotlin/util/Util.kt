package com.millburnx.cmdxpedro.util

import com.millburnx.cmdx.Command
import com.millburnx.cmdxpedro.util.geometry.vector.Vec2d
import com.millburnx.cmdxpedro.util.geometry.vector.Vec2f
import com.millburnx.cmdxpedro.util.geometry.vector.Vec2i
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.util.ElapsedTime

public suspend fun Command.WaitFor(case: () -> Boolean) {
    while (!case()) {
        sync()
    }
}

public suspend fun Command.SleepFor(ms: Long, earlyExit: () -> Boolean = { false }) {
    SleepFor(earlyExit) { ms }
}

public suspend fun Command.SleepFor(earlyExit: () -> Boolean = { false }, ms: () -> Long) {
    val elapsedTime = ElapsedTime()
    WaitFor { elapsedTime.milliseconds() >= ms() || earlyExit() }
}

public fun Double.toRadians(): Double = Math.toRadians(this)

public fun Double.toDegrees(): Double = Math.toDegrees(this)

public fun Vec2d.toPedro(): Pose = Pose(x, y)
public fun Vec2f.toPedro(): Pose = Pose(x.toDouble(), y.toDouble())
public fun Vec2i.toPedro(): Pose = Pose(x.toDouble(), y.toDouble())