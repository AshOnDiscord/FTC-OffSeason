package org.firstinspires.ftc.teamcode

import com.millburnx.cmdx.Command
import com.millburnx.cmdx.runtimeGroups.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "CommandBaseTest")
class CommandBaseTest : LinearOpMode() {
    val scheduler = CommandScheduler()

    override fun runOpMode() {
        scheduler.schedule(
            Command {
                println("Hello World!")
            },
        )
    }
}
