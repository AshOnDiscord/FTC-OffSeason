package com.millburnx.cmdxpedro.paths

import com.millburnx.cmdxpedro.paths.heading.HeadingInterpolation
import com.millburnx.cmdxpedro.paths.path.Path
import com.pedropathing.follower.Follower
import com.pedropathing.paths.PathBuilder
import com.pedropathing.paths.PathChain

public fun PedroPath(
    follower: Follower,
    path: Path,
    headingInterpolation: HeadingInterpolation,
    positionMirror: Boolean = false,
    headingMirror: Boolean = false,
    handleCallbacks: (PathBuilder) -> Unit = {},
): PathChain {
    val builder = follower.pathBuilder()
    path.register(builder, positionMirror)
    headingInterpolation.register(builder, headingMirror)
    handleCallbacks(builder)
    return builder.build()
}
