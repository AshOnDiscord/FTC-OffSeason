package com.millburnx.cmdxpedro.paths.path

import com.millburnx.cmdxpedro.util.mirror
import com.millburnx.cmdxpedro.util.toPedro
import com.millburnx.util.vector.Vec2d
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.paths.PathBuilder

public class CubicBezier(
    public val starting: Vec2d,
    public val p2: Vec2d,
    public val p3: Vec2d,
    public val ending: Vec2d
) : Path {
    override fun register(pathBuilder: PathBuilder, mirrored: Boolean): PathBuilder {
        return pathBuilder.apply {
            addPath(
                BezierCurve(
                    starting.toPedro().mirror(mirrored),
                    p2.toPedro().mirror(mirrored),
                    p3.toPedro().mirror(mirrored),
                    ending.toPedro().mirror(mirrored),
                )
            )
        }
    }
}