package com.millburnx.cmdxpedro

import com.millburnx.cmdx.Command
import com.millburnx.cmdxpedro.util.WaitFor
import com.pedropathing.follower.Follower
import com.pedropathing.paths.PathChain

public fun FollowPath(
    follower: Follower,
    path: PathChain,
    maxPower: Double,
    opModeIsActive: () -> Boolean,
    translationalTolerance: Double = 1.0,
    headingTolerance: Double = 5.0,
): Command =
    Command {
        follower.followPath(path, maxPower, true)
        WaitFor {
            !opModeIsActive() || !follower.isBusy || follower.atParametricEnd() || follower.isRobotStuck ||
                follower.atPose(path.endPose(), translationalTolerance, headingTolerance) ||
                follower.atPose(path.endPoint(), translationalTolerance, headingTolerance)
        }
    }

public fun TurnTo(
    follower: Follower,
    degrees: Double,
    opModeIsActive: () -> Boolean,
): Command =
    Command {
        follower.turnToDegrees(degrees)
        WaitFor { !opModeIsActive() || !(follower.isBusy || follower.isTurning) }
    }
