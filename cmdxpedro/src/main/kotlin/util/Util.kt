package com.millburnx.cmdxpedro.util

import com.millburnx.cmdx.Command
import com.millburnx.cmdxpedro.util.geometry.vector.Vec2d
import com.millburnx.cmdxpedro.util.geometry.vector.Vec2f
import com.millburnx.cmdxpedro.util.geometry.vector.Vec2i
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeDegrees
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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

public fun Pose2d.mirror(mirror: Boolean = true): Pose2d {
    if (!mirror) return this
    return Pose2d(144.0 - this.x, this.y, normalizeDegrees(180.0 - this.degrees))
}

public fun Vec2d.mirror(mirror: Boolean = true): Vec2d {
    if (!mirror) return this
    return Vec2d(144.0 - this.x, this.y)
}

public fun Double.normalizeRadians(): Double {
    return atan2(sin(this), cos(this))
}

public fun Double.normalize(): Double {
    return this.toRadians().normalizeRadians().toDegrees()
}

public fun Double.mirror(mirror: Boolean = true): Double {
    if (!mirror) return this
    return (180 - this).normalize()
}

public fun Double.mirrorRadians(): Double {
    return (Math.PI - this).normalizeRadians()
}

public fun Pose.mirror(mirror: Boolean = true): Pose {
    if (!mirror) return this
    return mirror()
}