package com.millburnx.cmdxpedro.paths.heading

import com.millburnx.cmdxpedro.util.mirror
import com.millburnx.util.vector.Vec2d
import com.pedropathing.paths.HeadingInterpolator
import com.pedropathing.paths.PathBuilder

public class PointHeading(public val targetPoint: Vec2d) : HeadingInterpolation {
    override fun register(pathBuilder: PathBuilder, mirrored: Boolean): PathBuilder =
        pathBuilder.apply {
            val point = targetPoint.mirror(mirrored)
            setHeadingInterpolation(
                HeadingInterpolator.facingPoint(point.x, point.y)
            )
        }
}