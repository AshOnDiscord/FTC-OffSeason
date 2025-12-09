package com.millburnx.pedroparser.types

import com.millburnx.util.vector.Vec2d
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RawPoint(
    val x: Double,
    val y: Double
)

public fun RawPoint.toVec(): Vec2d {
    return Vec2d(x, y)
}

@Serializable
public enum class RawHeadingTypes {
    @SerialName("linear") LINEAR,
    @SerialName("tangential") TANGENTIAL,
    @SerialName("constant") CONSTANT
}

@Serializable
public sealed class RawEndPoint {
    public abstract val x: Double
    public abstract val y: Double
    public abstract val reverse: Boolean

    // heading = tag field used as discriminator
    @Serializable
    @SerialName("tangential")
    public data class RawTangentialPoint(
        override val x: Double,
        override val y: Double,
        override val reverse: Boolean = false
    ) : RawEndPoint()

    @Serializable
    @SerialName("constant")
    public data class RawConstantPoint(
        override val x: Double,
        override val y: Double,
        val degrees: Double,
        override val reverse: Boolean = false
    ) : RawEndPoint()

    @Serializable
    @SerialName("linear")
    public data class RawLinearPoint(
        override val x: Double,
        override val y: Double,
        val startDeg: Double,
        val endDeg: Double,
        override val reverse: Boolean = false
    ) : RawEndPoint()
}

public fun RawEndPoint.toVec(): Vec2d {
    return Vec2d(x.toDouble(), y.toDouble())
}

@Serializable
public data class RawPath(
    val name: String,
    val endPoint: RawEndPoint,
    val controlPoints: List<RawPoint>,
    val color: String
)

@Serializable
public data class RawStartPoint(
    val x: Double,
    val y: Double,
    val heading: RawHeadingTypes,
    val startDeg: Double,
    val endDeg: Double
)

@Serializable
public data class RawSequence(
    val startPoint: RawStartPoint,
    val lines: List<RawPath>
)
