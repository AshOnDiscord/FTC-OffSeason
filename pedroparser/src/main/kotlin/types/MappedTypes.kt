package com.millburnx.pedroparser.types

import com.millburnx.util.Pose2d
import com.millburnx.util.vector.Vec2d

public data class Sequence(
    val startPose: Pose2d,
    val lines: List<Path>
) {
    public companion object {
        public fun fromRaw(sequence: RawSequence): Sequence {
            val startPose = Pose2d(
                sequence.startPoint.x,
                sequence.startPoint.y,
                sequence.startPoint.startDeg,
            )
            return Sequence(
                startPose = startPose,
                lines = sequence.lines.mapIndexed { index, line ->
                    val previous = sequence.lines.getOrNull(index - 1)
                    if (previous != null) {
                        Path.fromRaw(line, previous)
                    } else {
                        Path.fromRaw(line, startPose.position)
                    }
                }
            )
        }
    }
}

public data class Path(
    val name: String,
    val points: List<Vec2d>,
    val heading: HeadingInterpolation
) {
    public companion object {
        public fun fromRaw(path: RawPath, previous: Vec2d): Path {
            val headingInterpolation = when (path.endPoint) {
                is RawEndPoint.RawTangentialPoint -> HeadingInterpolation.Tangential(
                    path.endPoint.reverse
                )

                is RawEndPoint.RawConstantPoint -> HeadingInterpolation.Constant(
                    path.endPoint.degrees,
                    path.endPoint.reverse
                )

                is RawEndPoint.RawLinearPoint -> HeadingInterpolation.Linear(
                    path.endPoint.startDeg,
                    path.endPoint.endDeg,
                    path.endPoint.reverse
                )
            }

            return Path(
                path.name, listOf(
                    previous,
                    *path.controlPoints.map { it.toVec() }.toTypedArray(),
                    path.endPoint.toVec(),
                ), headingInterpolation
            )
        }

        public fun fromRaw(path: RawPath, previous: RawPath): Path {
            return fromRaw(path, previous.endPoint.toVec())
        }
    }
}

public enum class HeadingTypes {
    TANGENTIAL, CONSTANT, LINEAR
}

public sealed class HeadingInterpolation(
    public val type: HeadingTypes
) {
    public open val reverse: Boolean = false

    public data class Tangential(
        override val reverse: Boolean = false,
    ) : HeadingInterpolation(HeadingTypes.TANGENTIAL)

    public data class Constant(
        val heading: Double,
        override val reverse: Boolean = false
    ) : HeadingInterpolation(HeadingTypes.CONSTANT)

    public data class Linear(
        val start: Double,
        val end: Double,
        override val reverse: Boolean = false
    ) : HeadingInterpolation(HeadingTypes.LINEAR)
}