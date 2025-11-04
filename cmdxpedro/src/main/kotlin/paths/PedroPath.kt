package com.millburnx.cmdxpedro.paths

import com.millburnx.cmdxpedro.paths.heading.HeadingInterpolation
import com.millburnx.cmdxpedro.paths.path.Path
import com.pedropathing.follower.Follower
import com.pedropathing.paths.PathChain

public fun PedroPath(follower: Follower, path: Path, headingInterpolation: HeadingInterpolation): PathChain {
    val builder = follower.pathBuilder()
    path.register(builder)
    headingInterpolation.register(builder)
    return builder.build()
}