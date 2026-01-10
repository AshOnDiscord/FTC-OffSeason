package com.millburnx.cmdxpedro

import com.millburnx.cmdx.Command
import com.millburnx.cmdxpedro.util.WaitFor
import com.pedropathing.follower.Follower
import com.pedropathing.paths.PathChain

public fun FollowPath(follower: Follower, path: PathChain, maxPower:Double, opModeIsActive: () -> Boolean): Command = Command() {
    follower.followPath(path, maxPower, true)
    WaitFor { !opModeIsActive() || !(follower.isBusy) }
}

public fun TurnTo(follower: Follower, degrees: Double, opModeIsActive: () -> Boolean): Command = Command() {
    follower.turnToDegrees(degrees)
    WaitFor { !opModeIsActive() || !(follower.isBusy || follower.isTurning) }
}