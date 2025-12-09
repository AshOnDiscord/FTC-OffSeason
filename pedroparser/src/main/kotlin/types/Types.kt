package com.millburnx.pedroparser.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Point(
    val x: Double,
    val y: Double
)

@Serializable
public enum class HeadingType {
    @SerialName("linear") LINEAR,
    @SerialName("tangential") TANGENTIAL,
    @SerialName("constant") CONSTANT
}

@Serializable
public sealed class EndPoint {
    public abstract val x: Double
    public abstract val y: Double
    public abstract val reverse: Boolean

    // heading = tag field used as discriminator
    @Serializable
    @SerialName("tangential")
    public data class TangentialPoint(
        override val x: Double,
        override val y: Double,
        override val reverse: Boolean = false
    ) : EndPoint()

    @Serializable
    @SerialName("constant")
    public data class ConstantPoint(
        override val x: Double,
        override val y: Double,
        val degrees: Double,
        override val reverse: Boolean = false
    ) : EndPoint()

    @Serializable
    @SerialName("linear")
    public data class LinearPoint(
        override val x: Double,
        override val y: Double,
        val startDeg: Double,
        val endDeg: Double,
        override val reverse: Boolean = false
    ) : EndPoint()
}

@Serializable
public data class Path(
    val name: String,
    val endPoint: EndPoint,
    val controlPoints: List<Point>,
    val color: String
)

@Serializable
public data class StartPoint(
    val x: Double,
    val y: Double,
    val heading: HeadingType,
    val startDeg: Double,
    val endDeg: Double
)

@Serializable
public data class Sequence(
    val startPoint: StartPoint,
    val lines: List<Path>
)
