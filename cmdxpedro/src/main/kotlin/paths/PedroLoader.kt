package com.millburnx.cmdxpedro.paths

import android.os.Environment
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
}