package org.firstinspires.ftc.teamcode

import com.millburnx.cmdx.Command
import com.millburnx.cmdx.runtimeGroups.CommandScheduler
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs
import kotlin.math.max

@TeleOp(name = "CommandBaseTest")
class CommandBaseTest : LinearOpMode() {
    val scheduler = CommandScheduler()

    val fl by lazy { hardwareMap["fl"] as DcMotorEx }
    val bl by lazy { hardwareMap["bl"] as DcMotorEx }
    val fr by lazy { hardwareMap["fr"] as DcMotorEx }
    val br by lazy { hardwareMap["br"] as DcMotorEx }

    override fun runOpMode() {
        scheduler.schedule(
            Command {
                println("Hello World!")
            },
        )
        fl.direction = DcMotorSimple.Direction.REVERSE
        bl.direction = DcMotorSimple.Direction.REVERSE
        waitForStart()
        while (opModeIsActive() && !isStopRequested) {
            val y = -
            gamepad1.left_stick_y
            val x = gamepad1.left_stick_x * 1.1
            val rx = gamepad1.right_stick_x

            val denom = max(abs(y) + abs(x) + abs(rx), 1.0)
            fl.power = (y + x + rx) / denom
            bl.power = (y - x + rx) / denom
            fr.power = (y - x - rx) / denom
            br.power = (y + x - rx) / denom
        }
    }
}
