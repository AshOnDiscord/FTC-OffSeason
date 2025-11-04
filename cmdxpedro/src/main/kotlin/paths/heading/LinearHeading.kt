package com.millburnx.cmdxpedro.paths.heading

import com.millburnx.cmdxpedro.util.toRadians
import com.pedropathing.paths.PathBuilder

public class LinearHeading(public val startingHeading: Double, public val endingHeading: Double) :
    HeadingInterpolation {
    override fun register(pathBuilder: PathBuilder): PathBuilder =
        pathBuilder.apply {
            setLinearHeadingInterpolation(startingHeading.toRadians(), endingHeading.toRadians())
        }
}