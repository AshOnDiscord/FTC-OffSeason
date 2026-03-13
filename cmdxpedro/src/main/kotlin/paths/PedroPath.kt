package com.millburnx.cmdxpedro.paths

import com.millburnx.cmdxpedro.paths.heading.HeadingInterpolation
import com.millburnx.cmdxpedro.paths.path.Path
import com.pedropathing.follower.Follower
import com.pedropathing.paths.PathBuilder
import com.pedropathing.paths.PathChain

@Suppress("ktlint:standard:function-naming")
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

@Suppress("ktlint:standard:function-naming")
public fun PedroPath(
    follower: Follower,
    path: PedroPathData,
    positionMirror: Boolean = false,
    headingMirror: Boolean = false,
): PathChain {
    val builder = follower.pathBuilder()
    path.path.register(builder, positionMirror)
    path.headingInterpolation.register(builder, headingMirror)
    path.callbacks(builder)
    return builder.build()
}

@Suppress("ktlint:standard:function-naming")
public fun PedroPath(
    follower: Follower,
    pathChain: List<PedroPathData>,
    positionMirror: Boolean = false,
    headingMirror: Boolean = false,
): PathChain {
    val builder = follower.pathBuilder()
    pathChain.forEach {
        it.path.register(builder, positionMirror)
        it.headingInterpolation.register(builder, headingMirror)
        it.callbacks(builder)
    }
    return builder.build()
}

public data class PedroPathData(
    val path: Path,
    val headingInterpolation: HeadingInterpolation,
    val callbacks: (PathBuilder) -> Unit = {},
)
