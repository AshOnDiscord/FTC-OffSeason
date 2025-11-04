package com.millburnx.cmdxpedro.paths.heading

import com.millburnx.cmdxpedro.util.toRadians
import com.pedropathing.paths.PathBuilder

public class ConstantHeading(public val heading: Double) : HeadingInterpolation {
    override fun register(pathBuilder: PathBuilder): PathBuilder =
        pathBuilder.apply {
            setConstantHeadingInterpolation(heading.toRadians())
        }
}