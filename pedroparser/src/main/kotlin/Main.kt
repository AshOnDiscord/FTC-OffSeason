package com.millburnx.pedroparser

import com.millburnx.pedroparser.types.Sequence
import kotlinx.serialization.json.Json

public val json: Json = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "heading"
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public fun main() {
    val inputStream = object {}.javaClass.getResourceAsStream("/trajectory.pp")
        ?: throw IllegalArgumentException("Resource not found: trajectory.pp")

    val jsonText = inputStream.bufferedReader().use { it.readText() }

    println(json.decodeFromString<Sequence>(jsonText))
}