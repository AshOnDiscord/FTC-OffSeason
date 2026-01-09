package com.millburnx.cmdxpedro.paths.heading

import com.millburnx.cmdxpedro.util.mirror
import com.millburnx.util.toRadians
import com.pedropathing.paths.PathBuilder

public data class LinearHeading(public val startingHeading: Double, public val endingHeading: Double) :
    HeadingInterpolation {
    override fun register(pathBuilder: PathBuilder, mirrored: Boolean): PathBuilder =
        pathBuilder.apply {
            setLinearHeadingInterpolation(startingHeading.mirror(mirrored).toRadians(), endingHeading.mirror(mirrored).toRadians())
        }
}