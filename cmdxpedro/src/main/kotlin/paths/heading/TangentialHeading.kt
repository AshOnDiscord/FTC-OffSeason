package com.millburnx.cmdxpedro.paths.heading

import com.pedropathing.paths.PathBuilder

public class TangentialHeading(public val reverse: Boolean) : HeadingInterpolation {
    override fun register(pathBuilder: PathBuilder, mirrored: Boolean): PathBuilder =
        pathBuilder.apply {
            setTangentHeadingInterpolation()
            if (reverse) setReversed()
        }
}
