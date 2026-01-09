package com.millburnx.cmdxpedro.paths.path

import com.millburnx.cmdxpedro.util.mirror
import com.millburnx.cmdxpedro.util.toPedro
import com.millburnx.util.vector.Vec2d
import com.pedropathing.geometry.BezierLine
import com.pedropathing.paths.PathBuilder

public data class Line(public val starting: Vec2d, public val ending: Vec2d) : Path {
    override fun register(pathBuilder: PathBuilder, mirrored: Boolean): PathBuilder =
        pathBuilder.apply {
            addPath(
                BezierLine(
                    starting.toPedro().mirror(mirrored),
                    ending.toPedro().mirror(mirrored)
                )
            )
        }
}