package com.millburnx.pedroparser

import com.millburnx.pedroparser.types.RawSequence
import com.millburnx.pedroparser.types.Sequence
import kotlinx.serialization.json.Json

public val SequenceJson: Json = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "heading"
}

public fun decodeJson(json: String): RawSequence = SequenceJson.decodeFromString<RawSequence>(json)

public fun mapRawData(rawSequence: RawSequence): Sequence = Sequence.fromRaw(rawSequence)

public fun parseSequence(json: String): Sequence = mapRawData(decodeJson(json))
//
public fun main() {
    val inputStream = object {}.javaClass.getResourceAsStream("/trajectory.pp")
        ?: throw IllegalArgumentException("Resource not found: trajectory.pp")

    val jsonText = inputStream.bufferedReader().use { it.readText() }
    println(parseSequence(jsonText))
}