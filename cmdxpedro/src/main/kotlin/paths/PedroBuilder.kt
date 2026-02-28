package com.millburnx.cmdxpedro.paths

import com.millburnx.cmdx.Command
import com.millburnx.cmdxpedro.FollowPath
import com.millburnx.cmdxpedro.paths.heading.HeadingInterpolation
import com.millburnx.cmdxpedro.paths.path.Path
import com.pedropathing.follower.Follower
import com.pedropathing.paths.PathBuilder
import com.pedropathing.paths.PathChain

/**
 * Builder utility class for other utility wrappers, not required but improves QOL
 *
 * Recommend to use using a [with] block:
 * ```kotlin
 * val builder = PedroBuilder(isMirrored = true)
 *
 * with (builder) {
 *   val path = Path(...)
 *   // ...
 * }
 * ```
 */
@Suppress("FunctionName") // Function names are uppercased so they are identical to constructors
public class PedroBuilder(
    public val isMirrored: Boolean = false,
) {
    public fun Path(
        follower: Follower,
        path: Path,
        headingInterpolation: HeadingInterpolation,
        bypassPositionMirror: Boolean = false,
        bypassHeadingMirror: Boolean = false,
        pathCallback: (PathBuilder) -> Unit = {},
    ): PathChain =
        PedroPath(
            follower,
            path,
            headingInterpolation,
            if (bypassPositionMirror) false else isMirrored,
            if (bypassHeadingMirror) false else isMirrored,
            pathCallback,
        )

    public fun PathCommand(
        follower: Follower,
        path: Path,
        headingInterpolation: HeadingInterpolation,
        opModeIsActive: () -> Boolean,
        bypassPositionMirror: Boolean = false,
        bypassHeadingMirror: Boolean = false,
        maxPower: Double = 1.0,
        pathCallback: (PathBuilder) -> Unit = {},
    ): Command =
        FollowPath(
            follower,
            Path(
                follower,
                path,
                headingInterpolation,
                bypassPositionMirror,
                bypassHeadingMirror,
                pathCallback,
            ),
            maxPower,
            opModeIsActive,
        )
}
