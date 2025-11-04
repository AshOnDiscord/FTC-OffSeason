package com.millburnx.cmdxpedro.paths.heading

import com.millburnx.cmdxpedro.util.geometry.vector.Vec2d
import com.pedropathing.paths.HeadingInterpolator
import com.pedropathing.paths.PathBuilder

public class PointHeading(public val targetPoint: Vec2d) : HeadingInterpolation {
    override fun register(pathBuilder: PathBuilder): PathBuilder =
        pathBuilder.apply {
            setHeadingInterpolation(
                HeadingInterpolator.facingPoint(targetPoint.x, targetPoint.y)
            )
        }
}