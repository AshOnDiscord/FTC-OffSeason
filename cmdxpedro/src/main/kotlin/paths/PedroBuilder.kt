package com.millburnx.cmdxpedro.paths

import com.millburnx.cmdx.Command
import com.millburnx.cmdxpedro.FollowPath
import com.millburnx.cmdxpedro.paths.heading.HeadingInterpolation
import com.millburnx.cmdxpedro.paths.path.Path
import com.pedropathing.follower.Follower
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
public class PedroBuilder(public val isMirrored: Boolean = false) {
    public fun Path(
        follower: Follower,
        path: Path,
        headingInterpolation: HeadingInterpolation,
        bypassPositionMirror: Boolean = false,
        bypassHeadingMirror: Boolean = false
    ): PathChain {
        return PedroPath(
            follower,
            path,
            headingInterpolation,
            if (bypassPositionMirror) false else isMirrored,
            if (bypassHeadingMirror) false else isMirrored
        )
    }

    public fun PathCommand(
        follower: Follower,
        path: Path,
        headingInterpolation: HeadingInterpolation,
        opModeIsActive: () -> Boolean,
        bypassPositionMirror: Boolean = false,
        bypassHeadingMirror: Boolean = false
    ): Command {
        return FollowPath(
            follower,
            Path(
                follower,
                path,
                headingInterpolation,
                bypassPositionMirror,
                bypassHeadingMirror
            ),
            opModeIsActive,
        )
    }
}