package com.millburnx.cmdxpedro.paths

import android.os.Environment
import com.millburnx.cmdxpedro.paths.heading.ConstantHeading
import com.millburnx.cmdxpedro.paths.heading.HeadingInterpolation
import com.millburnx.cmdxpedro.paths.heading.LinearHeading
import com.millburnx.cmdxpedro.paths.heading.TangentialHeading
import com.millburnx.cmdxpedro.paths.path.CubicBezier
import com.millburnx.cmdxpedro.paths.path.Line
import com.millburnx.cmdxpedro.paths.path.Path
import com.millburnx.pedroparser.parseSequence
import com.millburnx.pedroparser.types.Sequence
import java.io.File

/**
 * @param path: relative path to root directory.
 */
public fun loadFile(path: String): String {
    val rootDir = Environment.getExternalStorageDirectory()
    val filePath = "$rootDir/$path"
    val file = File(filePath)
    return file.bufferedReader().use { it.readText() }
}

public object PedroLoader {
    public fun load(sequenceName: String): Sequence {
        val rawJson = loadFile(sequenceName)
        return parseSequence(rawJson)
    }

    public fun sequenceToPath(sequence: Sequence): List<Pair<Path, HeadingInterpolation>> {
        return sequence.lines.map { line ->
            when (line.points.size) {
                2 -> {
                    Line(line.points[0], line.points[1])
                }
                4 -> {
                    CubicBezier(line.points[0], line.points[1], line.points[2], line.points[3])
                }
                else -> {
                    throw Error("points count not supported ${line.points.size}")
                }
            } to convertedHeading(line.heading)
        }
    }

    private fun convertedHeading(heading: com.millburnx.pedroparser.types.HeadingInterpolation): HeadingInterpolation {
        return when (heading) {
            is com.millburnx.pedroparser.types.HeadingInterpolation.Constant -> {
                ConstantHeading(heading.heading)
            }
            is com.millburnx.pedroparser.types.HeadingInterpolation.Linear -> {
                LinearHeading(heading.start, heading.end)
            }
            is com.millburnx.pedroparser.types.HeadingInterpolation.Tangential -> {
                TangentialHeading(heading.reverse)
            }
        }
    }
}