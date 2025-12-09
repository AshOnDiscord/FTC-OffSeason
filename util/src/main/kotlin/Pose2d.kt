package com.millburnx.util

import com.millburnx.util.vector.Vec2
import com.millburnx.util.vector.Vec2d
import kotlin.math.absoluteValue
import kotlin.math.sign

public data class Pose2d(
    val position: Vec2d = Vec2d(),
    val heading: Double = 0.0,
) {
    public constructor(x: Double, y: Double, heading: Double) : this(Vec2d(x, y), heading)
    public constructor(x: Double, y: Double) : this(Vec2d(x, y), 0.0)
    public constructor(values: Array<Double>) : this(values[0], values[1], values[2])

    val x: Double
        get() = position.x
    val y: Double
        get() = position.y
    val degrees: Double
        get() = heading
    val radians: Double
        get() = Math.toRadians(heading)
    val rotation: Double
        get() = heading

    public companion object {
        public fun fromRadians(
            position: Vec2<*, *>,
            radians: Double,
        ): Pose2d = Pose2d(Vec2d(position.x.toDouble(), position.y.toDouble()), radians.toDegrees())

        public fun fromRadians(
            x: Double,
            y: Double,
            radians: Double,
        ): Pose2d = Pose2d(x, y, radians.toDegrees())
    }

    public operator fun unaryMinus(): Pose2d = Pose2d(-position, -heading)

    public fun flipPos(): Pose2d = Pose2d(-position, heading)

    public fun abs(): Pose2d = Pose2d(position.abs(), heading.absoluteValue)

    public operator fun plus(other: Pose2d): Pose2d =
        Pose2d(position + other.position, (heading + other.heading).normalize())

    public operator fun plus(other: Vec2<*, *>): Pose2d = Pose2d(position + other, heading)

    public operator fun minus(other: Pose2d): Pose2d =
        Pose2d(position - other.position, (heading - other.heading).normalize())

    public operator fun minus(other: Vec2<*, *>): Pose2d = Pose2d(position - other, heading)

    public operator fun times(other: Vec2<*, *>): Pose2d = Pose2d(position * other, heading)

    public operator fun times(scalar: Double): Pose2d = Pose2d(position * scalar, heading)

    public operator fun div(other: Vec2<*, *>): Pose2d = Pose2d(position / other, heading)

    public operator fun div(scalar: Double): Pose2d = Pose2d(position / scalar, heading)

    public fun distanceTo(pose: Pose2d): Double = position.distance(pose.position)

    public fun distanceTo(vec2d: Vec2<*, *>): Double = position.distance(vec2d)

    public fun angleTo(pose: Pose2d): Double = position.angleTo(pose.position)
    public fun angleTo(vec2d: Vec2<*, *>): Double = position.angleTo(vec2d)

    public fun sign(): Pose2d = Pose2d(position.sign(), heading.sign)
}