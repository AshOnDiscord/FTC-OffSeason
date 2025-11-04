package com.millburnx.cmdxpedro.paths.path

import com.millburnx.cmdxpedro.util.geometry.vector.Vec2d
import com.millburnx.cmdxpedro.util.toPedro
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.paths.PathBuilder

public class CubicBezier(
    public val starting: Vec2d,
    public val p2: Vec2d,
    public val p3: Vec2d,
    public val ending: Vec2d
) : Path {
    override fun register(pathBuilder: PathBuilder): PathBuilder {
        return pathBuilder.apply {
            addPath(
                BezierCurve(
                    starting.toPedro(),
                    p2.toPedro(),
                    p3.toPedro(),
                    ending.toPedro()
                )
            )
        }
    }
}